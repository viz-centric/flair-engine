package com.fbi.engine.service.auditlog;

import com.fbi.engine.domain.QueryAuditLog;
import com.fbi.engine.domain.QueryAuditLogMetadata;
import com.fbi.engine.repository.QueryAuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class QueryAuditLogService {

    private final QueryAuditLogRepository queryAuditLogRepository;

    @Async
    public void recordQuery(QueryLogParams params) {
        QueryAuditLog auditLog = new QueryAuditLog();
        auditLog.setActor(params.getUsername());
        auditLog.setConnectionLinkId(params.getConnection().getLinkId());
        auditLog.setCreatedDate(Instant.now());
        auditLog.setMeta(toMetadata(params.getMeta()));
        auditLog.setQuery(params.getQuery());
        queryAuditLogRepository.save(auditLog);
    }

    private QueryAuditLogMetadata toMetadata(QueryLogMeta meta) {
        if (meta == null) {
            return null;
        }
        QueryAuditLogMetadata metadata = new QueryAuditLogMetadata();
        metadata.setDashboardId(meta.getDashboardId());
        metadata.setDatasourceId(meta.getDatasourceId());
        return metadata;
    }
}
