package com.fbi.engine.service.auditlog;

import com.fbi.engine.domain.Connection;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder
@Data
public class QueryLogParams {
    private String username;
    private String query;
    private Connection connection;
    private Map<String, Object> meta;
}
