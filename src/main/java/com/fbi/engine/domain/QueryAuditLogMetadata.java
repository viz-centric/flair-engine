package com.fbi.engine.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryAuditLogMetadata {
    private String dashboardId;
    private String datasourceId;
    private String action;
}
