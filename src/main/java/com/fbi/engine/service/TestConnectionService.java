package com.fbi.engine.service;

import com.fbi.engine.domain.Connection;
import com.fbi.engine.query.QueryService;
import com.project.bi.query.FlairQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class TestConnectionService {

    private final QueryService queryService;

    public String testConnection(Connection connection) {

        log.info("Testing connection for {}", connection);

        FlairQuery query = new FlairQuery("SHOW TABLES LIMIT 1", false);
        String executeQuery = queryService.executeQuery(connection, query).getResult();

        log.debug("Test query executed {}", executeQuery);

        return executeQuery;
    }

}
