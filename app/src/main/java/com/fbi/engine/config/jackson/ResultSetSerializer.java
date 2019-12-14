package com.fbi.engine.config.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

public class ResultSetSerializer extends JsonSerializer<ResultSet> {


    @Override
    public Class<ResultSet> handledType() {
        return ResultSet.class;
    }

    @Override
    public void serialize(ResultSet rs, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int numColumns = rsmd.getColumnCount();
            String[] columnNames = new String[numColumns];
            int[] columnTypes = new int[numColumns];

            for (int i = 0; i < columnNames.length; i++) {
                columnNames[i] = rsmd.getColumnLabel(i + 1);
                columnTypes[i] = rsmd.getColumnType(i + 1);
            }

            jgen.writeStartArray();

            while (rs.next()) {

                boolean b;
                long l;
                double d;

                jgen.writeStartObject();

                for (int i = 0; i < columnNames.length; i++) {

                    jgen.writeFieldName(columnNames[i]);
                    switch (columnTypes[i]) {

                        case Types.INTEGER:
                            l = rs.getInt(i + 1);
                            if (rs.wasNull()) {
                                jgen.writeNull();
                            } else {
                                jgen.writeNumber(l);
                            }
                            break;

                        case Types.BIGINT:
                            l = rs.getLong(i + 1);
                            if (rs.wasNull()) {
                                jgen.writeNull();
                            } else {
                                jgen.writeNumber(l);
                            }
                            break;

                        case Types.DECIMAL:
                        case Types.NUMERIC:
                            jgen.writeNumber(rs.getBigDecimal(i + 1));
                            break;

                        case Types.FLOAT:
                        case Types.REAL:
                        case Types.DOUBLE:
                            d = rs.getDouble(i + 1);
                            if (rs.wasNull()) {
                                jgen.writeNull();
                            } else {
                                jgen.writeNumber(d);
                            }
                            break;

                        case Types.NVARCHAR:
                        case Types.VARCHAR:
                        case Types.LONGNVARCHAR:
                        case Types.LONGVARCHAR:
                            jgen.writeString(rs.getString(i + 1));
                            break;

                        case Types.BOOLEAN:
                        case Types.BIT:
                            b = rs.getBoolean(i + 1);
                            if (rs.wasNull()) {
                                jgen.writeNull();
                            } else {
                                jgen.writeBoolean(b);
                            }
                            break;

                        case Types.BINARY:
                        case Types.VARBINARY:
                        case Types.LONGVARBINARY:
                            jgen.writeBinary(rs.getBytes(i + 1));
                            break;

                        case Types.TINYINT:
                        case Types.SMALLINT:
                            l = rs.getShort(i + 1);
                            if (rs.wasNull()) {
                                jgen.writeNull();
                            } else {
                                jgen.writeNumber(l);
                            }
                            break;

                        case Types.DATE:
                            Date date = rs.getDate(i + 1);
                            if (rs.wasNull()) {
                                jgen.writeNull();
                            } else {
                                provider.defaultSerializeDateValue(date, jgen);
                            }
                            break;

                        case Types.TIMESTAMP:
                            Timestamp time = rs.getTimestamp(i + 1);
                            if (rs.wasNull()) {
                                jgen.writeNull();
                            } else {
                                provider.defaultSerializeDateValue(time, jgen);
                            }
                            break;

                        case Types.BLOB:
                            Blob blob = rs.getBlob(i);
                            if (rs.wasNull()) {
                                jgen.writeNull();
                            } else {
                                provider.defaultSerializeValue(blob.getBinaryStream(), jgen);
                                blob.free();
                            }
                            break;

                        case Types.CLOB:
                            Clob clob = rs.getClob(i);
                            if (rs.wasNull()) {
                                jgen.writeNull();
                            } else {
                                provider.defaultSerializeValue(clob.getCharacterStream(), jgen);
                                clob.free();
                            }
                            break;

                        case Types.ARRAY:
                            throw new RuntimeException("ResultSetSerializer not yet implemented for SQL type ARRAY");

                        case Types.STRUCT:
                            throw new RuntimeException("ResultSetSerializer not yet implemented for SQL type STRUCT");

                        case Types.DISTINCT:
                            throw new RuntimeException("ResultSetSerializer not yet implemented for SQL type DISTINCT");

                        case Types.REF:
                            throw new RuntimeException("ResultSetSerializer not yet implemented for SQL type REF");

                        case Types.JAVA_OBJECT:
                        default:
                            Object object = rs.getObject(i + 1);
                            if (rs.wasNull()) {
                                jgen.writeNull();
                            } else {
                                provider.defaultSerializeValue(object, jgen);
                            }
                            break;
                    }
                }

                jgen.writeEndObject();
            }

            jgen.writeEndArray();

        } catch (SQLException e) {
            throw new ResultSetSerializerException(e);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private class ResultSetSerializerException extends JsonProcessingException {
        public ResultSetSerializerException(Throwable e) {
            super(e);
        }
    }

}
