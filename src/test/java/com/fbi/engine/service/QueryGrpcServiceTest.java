package com.fbi.engine.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.domain.Connection;
import com.fbi.engine.query.QueryServiceImpl;
import com.fbi.engine.service.cache.CacheMetadata;
import com.fbi.engine.service.cache.CacheParams;
import com.fbi.engine.service.constant.GrpcErrors;
import com.fbi.engine.service.dto.ConnectionParameters;
import com.fbi.engine.service.dto.RunQueryResultDTO;
import com.fbi.engine.service.validators.QueryValidationResult;
import com.fbi.engine.service.validators.QueryValidator;
import com.flair.bi.messages.Query;
import com.flair.bi.messages.QueryResponse;
import com.flair.bi.messages.QueryValidationResponse;
import com.flair.bi.messages.RunQueryRequest;
import com.flair.bi.messages.RunQueryResponse;
import com.google.common.collect.ImmutableMap;
import com.project.bi.query.FlairQuery;
import com.project.bi.query.dto.QueryDTO;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class QueryGrpcServiceTest {

    @Mock
    private ConnectionService connectionService;

    @Mock
    private QueryServiceImpl queryService;

    @Mock
    private QueryValidator queryValidator;

    @Mock
    private QueryRunnerService queryRunService;

    private ObjectMapper objectMapper;

    private AbstractQueryGrpcService service;

    @Mock
    private ConnectionParameterService connectionParameterService;

    @Mock
    private ConnectionHelperService connectionHelperService;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        service = new MockQueryGrpcService(connectionService,
            queryService,
            queryValidator,
            objectMapper,
            queryRunService,
            connectionParameterService,
            connectionHelperService);
    }

    @Test
    public void validateReturnsErrorIfConnectionNull() {
        StreamObserver<QueryValidationResponse> observer = Mockito.mock(StreamObserver.class);
        service.validate(Query.newBuilder().build(), observer);

        verify(observer, times(1)).onError(any(StatusRuntimeException.class));
        verify(observer, times(0)).onNext(any(QueryValidationResponse.class));
        verify(observer, times(0)).onCompleted();
    }

    @Test
    public void validateReturnsSuccessIfValidationResultObtained() {
        StreamObserver<QueryValidationResponse> observer = Mockito.mock(StreamObserver.class);

        when(connectionService.findByConnectionLinkId("123"))
            .thenReturn(new Connection());
        when(queryValidator.validate(any(QueryDTO.class)))
            .thenReturn(new QueryValidationResult());

        doAnswer(invocationOnMock -> {
            QueryValidationResponse queryValidationResponse = invocationOnMock.getArgumentAt(0, QueryValidationResponse.class);
            assertEquals(QueryValidationResponse.ValidationResult.ValidationResultType.SUCCESS, queryValidationResponse.getValidationResult().getType());
            assertEquals("{}", queryValidationResponse.getValidationResult().getData());
            return queryValidationResponse;
        }).when(observer)
            .onNext(any(QueryValidationResponse.class));

        service.validate(Query.newBuilder().setSourceId("123").build(), observer);

        verify(observer, times(0)).onError(any(StatusRuntimeException.class));
        verify(observer, times(1)).onCompleted();
    }

    @Test
    public void getDataStreamError() {
        StreamObserver<QueryResponse> mock = Mockito.mock(StreamObserver.class);
        StreamObserver<Query> dataStream = service.getDataStream(mock);

        RuntimeException exception = new RuntimeException();
        dataStream.onError(exception);

        verify(mock, times(1)).onError(any(StatusRuntimeException.class));
        verify(mock, times(0)).onNext(any(QueryResponse.class));
        verify(mock, times(0)).onCompleted();
    }

    @Test
    public void getDataStreamCompleted() {
        StreamObserver<QueryResponse> mock = Mockito.mock(StreamObserver.class);
        StreamObserver<Query> dataStream = service.getDataStream(mock);

        dataStream.onCompleted();

        verify(mock, times(0)).onError(any(StatusRuntimeException.class));
        verify(mock, times(0)).onNext(any(QueryResponse.class));
        verify(mock, times(1)).onCompleted();
    }

    @Test
    public void getDataStreamNextFailsValidation() {
        StreamObserver<QueryResponse> mock = Mockito.mock(StreamObserver.class);
        StreamObserver<Query> dataStream = service.getDataStream(mock);

        when(queryValidator.validate(any(QueryDTO.class)))
            .thenReturn(new QueryValidationResult().setErrors(GrpcErrors.DUPLICATE_FEATURES));

        dataStream.onNext(Query.newBuilder().build());

        verify(mock, times(1)).onError(any(StatusRuntimeException.class));
        verify(mock, times(0)).onNext(any(QueryResponse.class));
        verify(mock, times(0)).onCompleted();
    }

    @Test
    public void getDataStreamNextSucceedsValidation() {
        StreamObserver<QueryResponse> mock = Mockito.mock(StreamObserver.class);
        StreamObserver<Query> dataStream = service.getDataStream(mock);

        when(queryValidator.validate(any(QueryDTO.class)))
            .thenReturn(new QueryValidationResult());
        Connection connection = new Connection();
        connection.setLinkId("1234");
        when(connectionService.findByConnectionLinkId(eq("one")))
            .thenReturn(connection);
        when(queryService.executeQuery(any(Connection.class), any(FlairQuery.class), any(CacheParams.class)))
            .thenReturn(new CacheMetadata().setResult("test"));
        when(connectionParameterService.getParameters(eq("1234")))
                .thenReturn(new ConnectionParameters(ImmutableMap.of()));

        dataStream.onNext(Query.newBuilder().setSourceId("one").build());

        verify(mock, times(0)).onError(any(StatusRuntimeException.class));
        verify(mock, times(1)).onNext(any(QueryResponse.class));
        verify(mock, times(0)).onCompleted();
    }

    @Test
    public void testRunQuerySuccessfully() {
        StreamObserver<RunQueryResponse> streamObserver = Mockito.mock(StreamObserver.class);

        Query query = Query.newBuilder()
            .setUserId("usr")
            .setQueryId("qry")
            .setSourceId("srcid")
            .setSource("src")
            .setDistinct(true)
            .setLimit(100L)
            .build();

        RunQueryRequest queryRequest = RunQueryRequest.newBuilder()
            .setRetrieveMeta(true)
            .setQuery(query)
            .build();

        doAnswer(invocationOnMock -> {
            RunQueryResponse response = invocationOnMock.getArgumentAt(0, RunQueryResponse.class);
            assertEquals("result", response.getResult());
            return response;
        }).when(streamObserver)
            .onNext(any(RunQueryResponse.class));

        when(queryRunService.runQuery(any(QueryDTO.class), eq("srcid")))
            .thenReturn(new RunQueryResultDTO().setRawResult("result").setResultCode(RunQueryResultDTO.Result.OK));

        service.runQuery(queryRequest, streamObserver);

        verify(streamObserver, times(0)).onError(any(StatusRuntimeException.class));
        verify(streamObserver, times(1)).onCompleted();
    }

    @Test
    public void testRunQueryProducesError() {
        StreamObserver<RunQueryResponse> streamObserver = Mockito.mock(StreamObserver.class);

        Query query = Query.newBuilder()
            .setUserId("usr")
            .setQueryId("qry")
            .setSourceId("srcid")
            .setSource("src")
            .setDistinct(true)
            .setLimit(100L)
            .build();

        RunQueryRequest queryRequest = RunQueryRequest.newBuilder()
            .setRetrieveMeta(true)
            .setQuery(query)
            .build();

        doAnswer(invocationOnMock -> {
            StatusRuntimeException response = invocationOnMock.getArgumentAt(0, StatusRuntimeException.class);
            assertEquals(Status.Code.INTERNAL, response.getStatus().getCode());
            assertEquals("{\"errorCode\":\"DATASOURCE_NOT_FOUND\"}", response.getStatus().getDescription());
            return response;
        }).when(streamObserver)
            .onError(any(StatusRuntimeException.class));

        when(queryRunService.runQuery(any(QueryDTO.class), eq("srcid")))
            .thenReturn(new RunQueryResultDTO()
                .setResultCode(RunQueryResultDTO.Result.DATASOURCE_NOT_FOUND));

        service.runQuery(queryRequest, streamObserver);

        verify(streamObserver, times(0)).onNext(any(RunQueryResponse.class));
        verify(streamObserver, times(0)).onCompleted();
    }
}
