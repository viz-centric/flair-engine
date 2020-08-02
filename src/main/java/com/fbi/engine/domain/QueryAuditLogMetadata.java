package com.fbi.engine.domain;

import lombok.Data;

@Data
public class QueryAuditLogMetadata {
    private Long dashboardId;
    private Long datasourceId;
}
