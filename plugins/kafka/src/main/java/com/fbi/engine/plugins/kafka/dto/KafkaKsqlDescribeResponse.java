package com.fbi.engine.plugins.kafka.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

// see https://docs.confluent.io/current/ksql/docs/developer-guide/api.html#ksql-rest-api-reference for reference
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class KafkaKsqlDescribeResponse {
    private SourceDescription sourceDescription;
    private String statementText;

    @JsonProperty("error_code")
    private String errorCode;
    private String message;

    @Data
    @Accessors(chain = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SourceDescription {
        private String name;
        private List<Field> fields;
        private String type; // STREAM or TABLE
        private String key; //
        private String timestamp; // The name of the timestamp column.
        private String format; // One of JSON, AVRO, or DELIMITED.
        private String topic;
        private boolean extended;
        private String statistics;
    }

    @Data
    @Accessors(chain = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Field {
        private String name;
        private Schema schema;
    }

    @Data
    @Accessors(chain = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Schema {
        private String type; // INTEGER, BIGINT, BOOLEAN, DOUBLE, STRING, MAP, ARRAY, or STRUCT.
        private Schema memberSchema; // For MAP and ARRAY types
        private List<String> fields; // For STRUCT types
    }
}
