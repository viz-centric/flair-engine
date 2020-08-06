package com.fbi.engine.service.auditlog;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder
@Data
public class QueryLogMeta {
    private String dashboardId;
    private String datasourceId;

    public static QueryLogMeta fromMap(Map<String, String> map) {
        return QueryLogMeta.builder()
                .datasourceId(map.get("datasourceId"))
                .dashboardId(map.get("dashboardId"))
                .build();
    }
}
