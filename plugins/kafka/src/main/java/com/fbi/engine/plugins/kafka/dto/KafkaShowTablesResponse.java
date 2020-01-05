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
public class KafkaShowTablesResponse {
    @JsonProperty("@type")
    private String type;
    private String statementText;
    private List<Table> tables;
    private List<Table> streams;

    @JsonProperty("error_code")
    private String errorCode;
    private String message;

    @Data
    @Accessors(chain = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Table {
        private String type;
        private String name;
        private String topic;
        private String format;
        private boolean isWindowed;
    }
}
