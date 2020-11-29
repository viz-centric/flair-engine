package com.fbi.engine.service;

import com.fbi.engine.config.grpc.Constant;
import com.fbi.engine.domain.Connection;
import com.fbi.engine.domain.details.BigqueryConnectionDetails;
import com.fbi.engine.domain.details.ConnectionDetails;
import com.fbi.engine.query.QueryService;
import com.fbi.engine.service.cache.QueryParams;
import com.project.bi.query.FlairQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class TestConnectionService {

    private final QueryService queryService;

    public String testConnection(Connection connection) {

        log.info("Testing connection for {}", connection);

        String userName = Constant.USERNAME_CONTEXT_KEY.get();
        log.debug("testConnection for username: {}", userName);

        ConnectionDetails details = connection.getDetails();
        String schema = "";
        if (details instanceof BigqueryConnectionDetails) {
            String schemaName = details.getDatabaseName();
            schema = StringUtils.isNotEmpty(schemaName) ? "(schema " + schemaName + ")" : "";
        }
        FlairQuery query = new FlairQuery("SHOW TABLES " + schema + " LIMIT 1", false);
        String executeQuery = queryService.executeQuery(QueryParams.builder()
                .connection(connection)
                .flairQuery(query)
                .username(userName)
                .build()).getResult();

        log.debug("Test query executed {}", executeQuery);

        return executeQuery;
    }

}
