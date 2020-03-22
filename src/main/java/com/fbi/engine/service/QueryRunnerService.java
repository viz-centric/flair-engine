package com.fbi.engine.service;

import com.fbi.engine.domain.Connection;
import com.fbi.engine.domain.query.Query;
import com.fbi.engine.query.QueryService;
import com.fbi.engine.service.cache.CacheMetadata;
import com.fbi.engine.service.cache.CacheParams;
import com.fbi.engine.service.dto.CompileQueryResultDTO;
import com.fbi.engine.service.dto.RunQueryResultDTO;
import com.project.bi.query.FlairQuery;
import com.project.bi.query.dto.QueryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueryRunnerService {

    private final ConnectionService connectionService;
    private final QueryService queryService;

    public RunQueryResultDTO runQuery(QueryDTO queryDTO, String datasourceId) {
        log.info("Running query for query name {} datasource id {}", queryDTO.getSource(), datasourceId);

        Connection conn = connectionService.findByConnectionLinkId(datasourceId);
        if (conn == null) {
            return new RunQueryResultDTO()
                .setResultCode(RunQueryResultDTO.Result.DATASOURCE_NOT_FOUND);
        }

        FlairQuery query = new FlairQuery(queryDTO.interpret(),
                queryDTO.isMetaRetrieved(), queryDTO.getSource());

        CacheMetadata cacheMetadata = queryService.executeQuery(conn, query, new CacheParams());

        String executeQuery = cacheMetadata.getResult();

        log.debug("Run query executed {}", executeQuery);

        if (StringUtils.isNotEmpty(executeQuery)) {
            return new RunQueryResultDTO()
                .setResultCode(RunQueryResultDTO.Result.OK)
                .setRawResult(executeQuery);
        }

        return new RunQueryResultDTO()
            .setResultCode(RunQueryResultDTO.Result.INVAILD_QUERY);
    }

    public CompileQueryResultDTO compileQuery(QueryDTO queryDTO, String datasourceId) {
        log.info("Running query for query name {} datasource id {}", queryDTO.getSource(), datasourceId);

        Connection conn = connectionService.findByConnectionLinkId(datasourceId);
        if (conn == null) {
            throw new IllegalArgumentException("Datasource not found for id " + datasourceId);
        }

        FlairQuery query = new FlairQuery(queryDTO.interpret(),
                queryDTO.isMetaRetrieved(), queryDTO.getSource());

        Query queryResult = queryService.compileQuery(conn, query);

        log.debug("Compile query executed {}", queryResult);

        return new CompileQueryResultDTO()
                .setRawQuery(queryResult.getQuery());
    }
}
