package com.fbi.engine.domain;

import lombok.Data;

@Data
public class QueryAuditLogMetadata {
    private String dashboardId;
    private String datasourceId;
}
