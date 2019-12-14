package com.fbi.engine.config.jackson;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.json.UTF8JsonGenerator;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.util.StringOutputStream;
import org.h2.tools.SimpleResultSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;

@RunWith(MockitoJUnitRunner.class)
public class ResultSetSerializerTest {

    private ResultSetSerializer serializer;
    private JsonGenerator generator;
    private StringWriter writer;

    @Before
    public void setUp() throws Exception {
        serializer = new ResultSetSerializer();

        JsonFactory factory = new JsonFactory();
        writer = new StringWriter();
        generator = factory.createGenerator(writer);
    }

    @Test
    public void serialize() throws IOException, SQLException {
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        ResultSetMetaData resultSetMetaData = Mockito.mock(ResultSetMetaData.class);
        Mockito.when(resultSet.getMetaData()).thenReturn(resultSetMetaData);
        Mockito.when(resultSetMetaData.getColumnCount()).thenReturn(1);
        Mockito.when(resultSetMetaData.getColumnLabel(eq(1))).thenReturn("column name");
        Mockito.when(resultSetMetaData.getColumnType(eq(1))).thenReturn(Types.VARCHAR);
        Mockito.when(resultSet.getString(eq(1))).thenReturn("data value");
        Mockito.when(resultSet.next()).thenReturn(true, false);

        serializer.serialize(resultSet, generator, new DefaultSerializerProvider.Impl());

        generator.flush();

        assertEquals("[{\"column name\":\"data value\"}]", writer.toString());
    }
}
