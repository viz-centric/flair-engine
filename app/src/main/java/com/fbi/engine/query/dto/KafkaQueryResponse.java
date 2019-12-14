package com.fbi.engine.query.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KafkaQueryResponse {
    private KafkaQueryRow row;
    private String finalMessage;
    private String errorMessage;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KafkaQueryRow {
        private List<Object> columns;
    }
}
