package com.fbi.engine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.config.grpc.Constant;
import com.fbi.engine.domain.Connection;
import com.fbi.engine.query.QueryService;
import com.fbi.engine.service.auditlog.QueryLogMeta;
import com.fbi.engine.service.cache.CacheMetadata;
import com.fbi.engine.service.cache.CacheParams;
import com.fbi.engine.service.cache.QueryParams;
import com.fbi.engine.service.dto.CompileQueryResultDTO;
import com.fbi.engine.service.dto.ConnectionParameters;
import com.fbi.engine.service.dto.RunQueryResultDTO;
import com.fbi.engine.service.util.QueryGrpcUtils;
import com.flair.bi.messages.Query;
import com.flair.bi.messages.QueryAllRequest;
import com.flair.bi.messages.QueryAllResponse;
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
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Optional;

import static com.fbi.engine.service.constant.GrpcConstants.ABORTED_INTERNAL;
import static com.fbi.engine.service.constant.GrpcConstants.CONNECTION_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractQueryGrpcService extends QueryServiceGrpc.QueryServiceImplBase {

    private final ConnectionService connectionService;

    private final QueryService queryService;

    private final ObjectMapper objectMapper;

    private final QueryRunnerService queryRunnerService;

    private final ConnectionParameterService connectionParameterService;

    private final ConnectionHelperService connectionHelperService;

    @Override
    public void queryAll(QueryAllRequest request, StreamObserver<QueryAllResponse> responseObserver) {
        log.debug("Query all {}", request);
        if (!request.hasConnection() && StringUtils.isEmpty(request.getConnectionLinkId())) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(CONNECTION_NOT_FOUND).asRuntimeException());
            return;
        }

        String userName = Constant.USERNAME_CONTEXT_KEY.get();
        log.debug("queryAll for username: {}", userName);

        Connection connection;
        if (request.hasConnection()) {
            connection = connectionHelperService.toConnectionEntity(request.getConnection());
        } else {
            connection = connectionService.findByConnectionLinkId(request.getConnectionLinkId());
        }

        if (connection == null) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(CONNECTION_NOT_FOUND).asRuntimeException());
            return;
        }

        QueryDTO queryDTO = QueryGrpcUtils.mapToQueryDTO(request.getQuery());
        queryDTO.setMetaRetrieved(true);
        FlairQuery flairQuery = new FlairQuery(queryDTO);
        CacheMetadata result = queryService.executeQuery(QueryParams.builder()
                .connection(connection)
                .flairQuery(flairQuery)
                .username(userName)
                .metadata(QueryLogMeta.fromMap(queryDTO.getMetadata()))
                .build());
        log.debug("Query all result request {}", flairQuery.getStatement());
//        log.info("Query all result result {}", result);

        responseObserver.onNext(QueryAllResponse.newBuilder()
                .setData(result.getResult())
                .setUserId(request.getQuery().getUserId())
                .setQueryId(request.getQuery().getQueryId())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void validate(Query query, StreamObserver<QueryValidationResponse> responseObserver) {
        log.info("Unary Validate Request received: {}", query);
        Connection connection = connectionService.findByConnectionLinkId(query.getSourceId());
        if (connection == null) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(CONNECTION_NOT_FOUND).asRuntimeException());
            return;
        }

        QueryDTO queryDTO = QueryGrpcUtils.mapToQueryDTO(query);

        CompileQueryResultDTO result = queryRunnerService.compileQuery(queryDTO, query.getSourceId());
        String rawQuery = result.getRawQuery();

        QueryValidationResponse queryValidationResponse = QueryValidationResponse.newBuilder()
            .setQueryId(query.getQueryId())
            .setUserId(query.getUserId())
            .setRawQuery(rawQuery)
            .setValidationResult(QueryValidationResponse.ValidationResult.newBuilder()
                .setType(QueryValidationResponse.ValidationResult.ValidationResultType.SUCCESS)
                .build())
            .build();

        responseObserver.onNext(queryValidationResponse);

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
            String userName = Constant.USERNAME_CONTEXT_KEY.get();
            log.debug("getData for username: {}", userName);
            FlairQuery flairQuery = new FlairQuery(queryDTO);
            String retVal = queryService.executeQuery(QueryParams.builder()
                    .connection(connection)
                    .flairQuery(flairQuery)
                    .username(userName)
                    .metadata(QueryLogMeta.fromMap(queryDTO.getMetadata()))
                    .build()).getResult();
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
                log.debug("Streaming Request received: {}", query);
                QueryDTO queryDTO = QueryGrpcUtils.mapToQueryDTO(query);
                log.debug("Streaming Request DTO  received: {}", queryDTO);

                Connection connection = connectionService.findByConnectionLinkId(query.getSourceId());
                if (connection == null) {
                    responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(CONNECTION_NOT_FOUND).asRuntimeException());
                    return;
                }

                ConnectionParameters connectionParameters = connectionParameterService.getParameters(connection.getLinkId());

                int cachePurgeAfterMinutes = connectionParameters.getCachePurgeAfterMinutes();
                int refreshAfterTimesRead = connectionParameters.getRefreshAfterTimesRead();
                int refreshAfterMinutes = connectionParameters.getRefreshAfterMinutes();

                FlairQuery flairQuery = new FlairQuery(queryDTO);

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

//                log.debug("Query being executed result {}: {}", cacheMetadata, cacheMetadata.getResult());

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
        String userName = Constant.USERNAME_CONTEXT_KEY.get();
        log.debug("getDataStream for username: {}", userName);

        CacheMetadata cacheMetadata = queryService.executeQuery(QueryParams.builder()
                .connection(connection)
                .flairQuery(flairQuery)
                .cacheParams(cacheParams)
                .username(userName)
                .metadata(QueryLogMeta.fromMap(query.getMetaMap()))
                .build());

        responseObserver.onNext(QueryResponse.newBuilder()
                .setQueryId(query.getQueryId())
                .setUserId(query.getUserId())
                .setData(Optional.ofNullable(cacheMetadata.getResult()).orElse(""))
                .setCacheMetadata(com.flair.bi.messages.CacheMetadata.newBuilder()
                        .setStale(cacheMetadata.isStale())
                        .setDateCreated(cacheMetadata.getDateCreated() != null ? cacheMetadata.getDateCreated().getEpochSecond() : 0)
                        .build())
                .build());

        return cacheMetadata;
    }

    @Override
    public void runQuery(RunQueryRequest request, StreamObserver<RunQueryResponse> responseObserver) {
        log.debug("Run query invoked {}", request);

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
