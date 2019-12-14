package com.fbi.engine.query;

import com.fbi.engine.config.FlairCachingConfig;
import com.fbi.engine.domain.Connection;
import com.fbi.engine.domain.ConnectionType;
import com.fbi.engine.domain.query.Query;
import com.fbi.engine.query.abstractfactory.QueryAbstractFactory;
import com.fbi.engine.query.factory.FlairFactory;
import com.fbi.engine.service.cache.CacheMetadata;
import com.fbi.engine.service.cache.CacheParams;
import com.fbi.engine.service.cache.FlairCachingService;
import com.project.bi.query.FlairCompiler;
import com.project.bi.query.FlairQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.Writer;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class QueryServiceImplTest {

    @Mock
    QueryAbstractFactory queryAbstractFactory;

    @Mock
    FlairCachingService flairCachingService;

    @Mock
    FlairCachingConfig flairCachingConfig;

    private QueryServiceImpl service;

    @Before
    public void setUp() throws Exception {
        service = new QueryServiceImpl(queryAbstractFactory, flairCachingService, flairCachingConfig);
        when(flairCachingConfig.isEnabled()).thenReturn(true);
    }

    @Test
    public void executeQuery() throws Exception {
        Connection connection = new Connection();
        ConnectionType connectionType = new ConnectionType();
        connectionType.setBundleClass("bundleClass");
        connection.setConnectionType(connectionType);

        FlairQuery flairQuery = new FlairQuery("statement", true);

        FlairFactory flairFactory = mock(FlairFactory.class);
        FlairCompiler flairCompiler = mock(FlairCompiler.class);
        QueryExecutor queryExecutor = mock(QueryExecutor.class);
        Query query = mock(Query.class);

        when(flairFactory.getCompiler()).thenReturn(flairCompiler);
        when(flairFactory.getExecutor(eq(connection))).thenReturn(queryExecutor);
        when(flairFactory.getQuery(eq(flairQuery), eq(""))).thenReturn(query);

        when(queryAbstractFactory.getQueryFactory(eq("bundleClass"))).thenReturn(flairFactory);

        service.executeQuery(connection, flairQuery);

        verify(flairCompiler, times(1)).compile(eq(flairQuery), any(Writer.class));
        verify(queryExecutor, times(1)).execute(eq(query), any(Writer.class));
    }

    @Test
    public void executeQueryWithReadCache() throws Exception {
        Connection connection = new Connection();
        connection.setLinkId("1234");
        ConnectionType connectionType = new ConnectionType();
        connectionType.setBundleClass("bundleClass");
        connection.setConnectionType(connectionType);

        FlairQuery flairQuery = new FlairQuery("statement", true);

        FlairFactory flairFactory = mock(FlairFactory.class);
        FlairCompiler flairCompiler = mock(FlairCompiler.class);
        QueryExecutor queryExecutor = mock(QueryExecutor.class);
        Query query = mock(Query.class);

        when(flairFactory.getCompiler()).thenReturn(flairCompiler);
        when(flairFactory.getExecutor(eq(connection))).thenReturn(queryExecutor);
        when(flairFactory.getQuery(eq(flairQuery), eq(""))).thenReturn(query);

        when(queryAbstractFactory.getQueryFactory(eq("bundleClass"))).thenReturn(flairFactory);
        when(flairCachingService.getResult(eq(flairQuery), eq(connection.getLinkId())))
                .thenReturn(Optional.of(new CacheMetadata().setResult("result")));

        CacheMetadata cacheMetadata = service.executeQuery(connection, flairQuery, new CacheParams().setReadFromCache(true));

        assertEquals("result", cacheMetadata.getResult());
    }

    @Test
    public void executeQueryWithReadCacheIsDisabled() throws Exception {
        Connection connection = new Connection();
        connection.setLinkId("1234");
        ConnectionType connectionType = new ConnectionType();
        connectionType.setBundleClass("bundleClass");
        connection.setConnectionType(connectionType);

        FlairQuery flairQuery = new FlairQuery("statement", true);

        FlairFactory flairFactory = mock(FlairFactory.class);
        FlairCompiler flairCompiler = mock(FlairCompiler.class);
        QueryExecutor queryExecutor = mock(QueryExecutor.class);
        Query query = mock(Query.class);

        when(flairFactory.getCompiler()).thenReturn(flairCompiler);
        when(flairFactory.getExecutor(eq(connection))).thenReturn(queryExecutor);
        when(flairFactory.getQuery(eq(flairQuery), eq(""))).thenReturn(query);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Writer writer = invocationOnMock.getArgumentAt(1, Writer.class);
                writer.write("some result");
                return null;
            }
        }).when(queryExecutor).execute(eq(query), any(Writer.class));

        when(queryAbstractFactory.getQueryFactory(eq("bundleClass"))).thenReturn(flairFactory);
        when(flairCachingService.getResult(eq(flairQuery), eq(connection.getLinkId())))
                .thenReturn(Optional.of(new CacheMetadata().setResult("result")));

        CacheMetadata cacheMetadata = service.executeQuery(connection, flairQuery, new CacheParams().setReadFromCache(false).setWriteToCache(true));

        assertEquals("some result", cacheMetadata.getResult());
        verify(flairCachingService, times(1))
                .putResultAsync(eq(flairQuery), eq(connection.getLinkId()), eq("some result"), any(CacheParams.class));
    }

    @Test
    public void executeQueryWithReadCacheCausesToReadDbIfNoCache() throws Exception {
        Connection connection = new Connection();
        connection.setLinkId("1234");
        ConnectionType connectionType = new ConnectionType();
        connectionType.setBundleClass("bundleClass");
        connection.setConnectionType(connectionType);

        FlairQuery flairQuery = new FlairQuery("statement", true);

        FlairFactory flairFactory = mock(FlairFactory.class);
        FlairCompiler flairCompiler = mock(FlairCompiler.class);
        QueryExecutor queryExecutor = mock(QueryExecutor.class);
        Query query = mock(Query.class);

        when(flairFactory.getCompiler()).thenReturn(flairCompiler);
        when(flairFactory.getExecutor(eq(connection))).thenReturn(queryExecutor);
        when(flairFactory.getQuery(eq(flairQuery), eq(""))).thenReturn(query);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Writer writer = invocationOnMock.getArgumentAt(1, Writer.class);
                writer.write("some result");
                return null;
            }
        }).when(queryExecutor).execute(eq(query), any(Writer.class));

        when(queryAbstractFactory.getQueryFactory(eq("bundleClass"))).thenReturn(flairFactory);
        when(flairCachingService.getResult(eq(flairQuery), eq(connection.getLinkId())))
                .thenReturn(Optional.empty());

        CacheMetadata cacheMetadata = service.executeQuery(connection, flairQuery, new CacheParams().setReadFromCache(true).setWriteToCache(true));

        assertEquals("some result", cacheMetadata.getResult());
        verify(flairCachingService, times(1))
                .putResultAsync(eq(flairQuery), eq(connection.getLinkId()), eq("some result"), any(CacheParams.class));
    }

}
