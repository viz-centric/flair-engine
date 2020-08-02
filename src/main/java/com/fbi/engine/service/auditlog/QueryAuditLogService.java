package com.fbi.engine.service.auditlog;

import com.fbi.engine.repository.QueryAuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class QueryAuditLogService {

    private final QueryAuditLogRepository queryAuditLogRepository;


}
