package com.fbi.engine.query;

import com.fbi.engine.domain.Connection;
import com.fbi.engine.domain.ConnectionType;
import com.fbi.engine.domain.query.Query;
import com.fbi.engine.query.abstractfactory.QueryAbstractFactory;
import com.fbi.engine.query.factory.FlairFactory;
import com.project.bi.query.FlairCompiler;
import com.project.bi.query.FlairQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.Writer;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class QueryServiceImplTest {

    @Mock
    QueryAbstractFactory queryAbstractFactory;

    private QueryServiceImpl service;

    @Before
    public void setUp() throws Exception {
        service = new QueryServiceImpl(queryAbstractFactory);
    }

    @Test
    public void executeQuery() throws Exception {
        Connection connection = new Connection();
        ConnectionType connectionType = new ConnectionType();
        connectionType.setBundleClass("bundleClass");
        connection.setConnectionType(connectionType);

        FlairQuery flairQuery = new FlairQuery();

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
}
