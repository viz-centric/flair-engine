package com.fbi.engine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.domain.Connection;
import com.fbi.engine.query.QueryServiceImpl;
import com.fbi.engine.service.cache.CacheMetadata;
import com.fbi.engine.service.cache.CacheParams;
import com.fbi.engine.service.dto.ConnectionParameters;
import com.fbi.engine.service.dto.RunQueryResultDTO;
import com.fbi.engine.service.util.QueryGrpcUtils;
import com.fbi.engine.service.validators.QueryValidationResult;
import com.fbi.engine.service.validators.QueryValidator;
import com.flair.bi.messages.Query;
import com.flair.bi.messages.QueryResponse;
import com.flair.bi.messages.QueryServiceGrpc;
import com.flair.bi.messages.QueryValidationResponse;
import com.flair.bi.messages.RunQueryRequest;
import com.flair.bi.messages.RunQueryResponse;
import com.project.bi.query.FlairQuery;
import com.project.bi.query.dto.QueryDTO;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

import static com.fbi.engine.service.constant.GrpcConstants.ABORTED_INTERNAL;
import static com.fbi.engine.service.constant.GrpcConstants.CONNECTION_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractQueryGrpcService extends QueryServiceGrpc.QueryServiceImplBase {

    private final ConnectionService connectionService;

    private final QueryServiceImpl queryService;

    private final QueryValidator queryValidator;

    private final ObjectMapper objectMapper;

    private final QueryRunnerService queryRunnerService;

    private final ConnectionParameterService connectionParameterService;

    @Override
    public void validate(Query query, StreamObserver<QueryValidationResponse> responseObserver) {
        log.info("Unary Validate Request received: {}", query);
        Connection connection = connectionService.findByConnectionLinkId(query.getSourceId());
        if (connection == null) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(CONNECTION_NOT_FOUND).asRuntimeException());
            return;
        }

        QueryDTO queryDTO = QueryGrpcUtils.mapToQueryDTO(query);
        QueryValidationResult queryValidationResult = queryValidator.validate(queryDTO);

        QueryValidationResponse queryValidationResponse = QueryValidationResponse.newBuilder()
            .setQueryId(query.getQueryId())
            .setUserId(query.getUserId())
            .setRawQuery(queryDTO.interpret(connection.getName()))
            .setValidationResult(QueryValidationResponse.ValidationResult.newBuilder()
                .setType(queryValidationResult.isError() ?
                    QueryValidationResponse.ValidationResult.ValidationResultType.INVALID :
                    QueryValidationResponse.ValidationResult.ValidationResultType.SUCCESS)
                .setData(queryValidationAsJson(queryValidationResult))
                .build())
            .build();

        responseObserver.onNext(queryValidationResponse);

        log.debug("Sending query validation result: {}", queryValidationResult);

        responseObserver.onCompleted();
    }

    @Override
    public void getData(Query request, StreamObserver<QueryResponse> responseObserver) {
        log.info("Unary Request received: {}", request.toString());
        Connection connection = connectionService.findByConnectionLinkId(request.getSourceId());
        if (connection == null) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(CONNECTION_NOT_FOUND).asRuntimeException());
        } else {
            QueryDTO queryDTO = QueryGrpcUtils.mapToQueryDTO(request);
            log.info("Interpreted query {}", queryDTO.toString());
            FlairQuery flairQuery = new FlairQuery(queryDTO.interpret(), queryDTO.isMetaRetrieved());
            String retVal = queryService.executeQuery(connection, flairQuery).getResult();
            responseObserver.onNext(QueryResponse.newBuilder()
                .setQueryId(request.getQueryId())
                .setUserId(request.getUserId())
                .setData(retVal)
                .build()
            );
            log.debug("Sending Out: {}", retVal);
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<Query> getDataStream(StreamObserver<QueryResponse> responseObserver) {
        return new StreamObserver<Query>() {
            @Override
            public void onNext(Query query) {
                log.debug("Streaming Request received: {}", query.toString());
                QueryDTO queryDTO = QueryGrpcUtils.mapToQueryDTO(query);
                if (!validateQuery(queryDTO, responseObserver)) {
                    return;
                }

                Connection connection = connectionService.findByConnectionLinkId(query.getSourceId());
                if (connection == null) {
                    responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(CONNECTION_NOT_FOUND).asRuntimeException());
                    return;
                }

                ConnectionParameters connectionParameters = connectionParameterService.getParameters(connection.getLinkId());

                int cachePurgeAfterMinutes = connectionParameters.getCachePurgeAfterMinutes();
                int refreshAfterTimesRead = connectionParameters.getRefreshAfterTimesRead();
                int refreshAfterMinutes = connectionParameters.getRefreshAfterMinutes();

                FlairQuery flairQuery = new FlairQuery(queryDTO.interpret(), queryDTO.isMetaRetrieved());

                log.debug("Query being executed {}", flairQuery.getStatement());

                CacheMetadata cacheMetadata = queryDataAndSendResult(query,
                        flairQuery,
                        connection,
                        responseObserver,
                        new CacheParams()
                                .setRefreshAfterTimesRead(refreshAfterTimesRead)
                                .setRefreshAfterMinutes(refreshAfterMinutes)
                                .setCachePurgeAfterMinutes(cachePurgeAfterMinutes)
                                .setReadFromCache(connectionParameters.isEnabled())
                                .setWriteToCache(connectionParameters.isEnabled()));

                log.debug("Query being executed result {}", cacheMetadata);

                if (cacheMetadata.isStale()) {
                    log.debug("Cache is stale, fetching new data {}", cacheMetadata);

                    CacheMetadata cacheMetadata2 = queryDataAndSendResult(query,
                            flairQuery,
                            connection,
                            responseObserver,
                            new CacheParams()
                                    .setRefreshAfterTimesRead(refreshAfterTimesRead)
                                    .setRefreshAfterMinutes(refreshAfterMinutes)
                                    .setCachePurgeAfterMinutes(cachePurgeAfterMinutes)
                                    .setReadFromCache(false)
                                    .setWriteToCache(connectionParameters.isEnabled()));

                    log.debug("After cache update {}", cacheMetadata2);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                responseObserver.onError(Status.ABORTED.withDescription(ABORTED_INTERNAL).asRuntimeException());
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    private CacheMetadata queryDataAndSendResult(Query query, FlairQuery flairQuery, Connection connection, StreamObserver<QueryResponse> responseObserver, CacheParams cacheParams) {
        CacheMetadata cacheMetadata = queryService.executeQuery(connection, flairQuery, cacheParams);

        responseObserver.onNext(QueryResponse.newBuilder()
                .setQueryId(query.getQueryId())
                .setUserId(query.getUserId())
                .setData(cacheMetadata.getResult())
                .setCacheMetadata(com.flair.bi.messages.CacheMetadata.newBuilder()
                        .setStale(cacheMetadata.isStale())
                        .setDateCreated(cacheMetadata.getDateCreated() != null ? cacheMetadata.getDateCreated().getEpochSecond() : 0)
                        .build())
                .build());

        return cacheMetadata;
    }

    @Override
    public void runQuery(RunQueryRequest request, StreamObserver<RunQueryResponse> responseObserver) {
        log.info("Run query invoked {}", request);

        QueryDTO queryDTO = QueryGrpcUtils.mapToQueryDTO(request.getQuery());
        queryDTO.setMetaRetrieved(request.getRetrieveMeta());

        RunQueryResultDTO result = queryRunnerService.runQuery(queryDTO, request.getQuery().getSourceId());

        if (result.getResultCode() == RunQueryResultDTO.Result.OK) {
            responseObserver.onNext(RunQueryResponse.newBuilder()
                .setResult(result.getRawResult())
                .build());
            responseObserver.onCompleted();
        } else {
            String paramsStr = queryExecutionAsJson(result);
            responseObserver.onError(Status.INTERNAL.withDescription(paramsStr).asRuntimeException());
        }
    }

    private boolean validateQuery(QueryDTO queryDTO, StreamObserver<?> responseObserver) {
        QueryValidationResult result = queryValidator.validate(queryDTO);
        log.debug("Validating query duplicates {}", result);
        if (result.isError()) {
            String paramsStr = queryValidationAsJson(result);
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(paramsStr).asRuntimeException());
            return false;
        }
        return true;
    }

    private String queryValidationAsJson(QueryValidationResult result) {
        HashMap<String, Object> params = new HashMap<>();
        if (result.getErrors() != null) {
            params.put("errorCode", result.getErrors().getErrorCode());
        }
        if (result.getFeatureNames() != null) {
            params.put("features", result.getFeatureNames());
        }
        String paramsStr;
        try {
            paramsStr = objectMapper.writeValueAsString(params);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error creating a json string from validation query parameters", e);
        }
        return paramsStr;
    }

    private String queryExecutionAsJson(RunQueryResultDTO result) {
        HashMap<String, Object> params = new HashMap<>();
        if (result.getResultCode() == RunQueryResultDTO.Result.DATASOURCE_NOT_FOUND) {
            params.put("errorCode", result.getResultCode().name());
        }
        String paramsStr;
        try {
            paramsStr = objectMapper.writeValueAsString(params);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error creating a json string from running query", e);
        }
        return paramsStr;
    }
}
