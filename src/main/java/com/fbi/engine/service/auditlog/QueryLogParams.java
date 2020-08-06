package com.fbi.engine.service.auditlog;

import com.fbi.engine.domain.Connection;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class QueryLogParams {
    private String username;
    private String query;
    private Connection connection;
    private QueryLogMeta meta;
}
