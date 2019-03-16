package com.fbi.engine.service;

import com.fbi.engine.domain.Connection;
import com.fbi.engine.query.QueryService;
import com.project.bi.query.FlairQuery;
import com.project.bi.query.dto.QueryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class TestConnectionService {

    private final QueryService queryService;
    private final ConnectionService connectionService;

    public String testConnection(String connectionLinkId, String datasourceName, Connection connection) {
        Connection conn;
        if (!StringUtils.isEmpty(connectionLinkId)) {
            conn = connectionService.findByConnectionLinkId(connectionLinkId);
        } else {
            conn = connection;
        }

        if (conn == null) {
            return null;
        }

        QueryDTO queryDTO = new QueryDTO();
        queryDTO.setSource(datasourceName);
        queryDTO.setLimit(1L);

        FlairQuery query = new FlairQuery(queryDTO.interpret(), queryDTO.isMetaRetrieved(), datasourceName);
        String executeQuery = queryService.executeQuery(conn, query).getResult();

        log.debug("Test query executed {}", executeQuery);

        return executeQuery;
    }

}
