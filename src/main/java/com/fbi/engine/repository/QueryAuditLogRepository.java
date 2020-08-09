package com.fbi.engine.repository;

import com.fbi.engine.domain.QueryAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QueryAuditLogRepository extends JpaRepository<QueryAuditLog, Long> {

}
