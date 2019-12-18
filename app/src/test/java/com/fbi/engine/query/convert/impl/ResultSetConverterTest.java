package com.fbi.engine.query.convert.impl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public class ResultSetConverterTest {

    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void convert() throws SQLException, IOException {
        ResultSetConverter converter = new ResultSetConverter(objectMapper, false);
        String result = converter.convert(new MockResultSet());

        Map mapExpected = objectMapper.readValue("{\"data\":{\"type\":0,\"closed\":false,\"warnings\":null,\"cursorName\":null,\"metaData\":{\"columnCount\":1},\"beforeFirst\":false,\"afterLast\":false,\"first\":false,\"last\":false,\"row\":0,\"fetchDirection\":0,\"fetchSize\":0,\"concurrency\":0,\"statement\":null,\"holdability\":0}}", Map.class);
        Map mapActual = objectMapper.readValue(result, Map.class);

        assertEquals(mapExpected, mapActual);
    }

    @Test
    public void convertWithMetadata() throws SQLException, IOException {
        MockResultSet resultSet = new MockResultSet();
        ResultSetConverter converter = new ResultSetConverter(new ObjectMapper(), true);
        String result = converter.convert(resultSet);

        Map mapExpected = objectMapper.readValue("{\"data\":{\"type\":0,\"closed\":false,\"warnings\":null,\"cursorName\":null,\"metaData\":{\"columnCount\":1},\"beforeFirst\":false,\"afterLast\":false,\"first\":false,\"last\":false,\"row\":0,\"fetchDirection\":0,\"fetchSize\":0,\"concurrency\":0,\"statement\":null,\"holdability\":0},\"metadata\":{\"column_name\":\"col_type_nam\"}}", Map.class);
        Map mapActual = objectMapper.readValue(result, Map.class);

        assertEquals(mapExpected, mapActual);
    }

}
