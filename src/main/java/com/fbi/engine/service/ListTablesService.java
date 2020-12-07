package com.fbi.engine.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.config.grpc.Constant;
import com.fbi.engine.domain.Connection;
import com.fbi.engine.query.QueryService;
import com.fbi.engine.service.cache.QueryParams;
import com.project.bi.query.FlairQuery;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.project.bi.query.SQLUtil.sanitize;

@Slf4j
@RequiredArgsConstructor
@Service
public class ListTablesService {

    private final QueryService queryService;
    private final ConnectionService connectionService;
    private final ObjectMapper objectMapper;

    public Set<String> listTables(String connectionLinkId, String tableNameLike, int maxEntries, Connection connection) {
        com.fbi.engine.domain.Connection conn;
        if (!StringUtils.isEmpty(connectionLinkId)) {
            log.info("List tables for link ID {} and table name {}", connectionLinkId, tableNameLike);
            conn = connectionService.findByConnectionLinkId(connectionLinkId);
        } else {
            conn = connection;
        }

        if (conn == null) {
            log.info("List tables for connection is null for table name {}", tableNameLike);
            return null;
        }

        log.info("List tables for connection {}", conn.getName());

        String schemaName = conn.getDetails().getDatabaseName();

        HashSet<String> sets = new HashSet<>(executeQuery(tableNameLike, maxEntries, conn, schemaName));
        Set<String> strings = sets
                .stream()
                .filter(item -> item.toUpperCase().contains(tableNameLike.toUpperCase()))
                .limit(maxEntries)
                .collect(Collectors.toSet());
        log.debug("List tables result {}", strings);
        return strings;
    }

    private Set<String> executeQuery(String tableNameLike, int maxEntries, Connection conn, String schemaName) {
        String userName = Constant.USERNAME_CONTEXT_KEY.get();
        log.debug("listTables for username: {}", userName);

        String schema = StringUtils.isNotEmpty(schemaName) ? "(schema " + schemaName + ")" : "";
        FlairQuery query = new FlairQuery("SHOW TABLES " + schema + " LIKE '%" + sanitize(tableNameLike) + "%' LIMIT " + maxEntries, false);
        String executeQuery = queryService.executeQuery(QueryParams.builder()
                .connection(conn)
                .flairQuery(query)
                .username(userName)
                .build())
                .getResult();

        log.debug("List tables query executed {}", executeQuery);

        RowResult map;
        try {
            map = objectMapper.readValue(executeQuery, RowResult.class);
        } catch (IOException e) {
            log.error("Error converting result into json for " + tableNameLike, e);
            return new HashSet<>();
        }
        return map.getData()
                .stream()
                .map(item -> item.values())
                .flatMap((Function<Collection, Stream<String>>) collection -> collection.stream().map(item -> String.valueOf(item)))
                .collect(Collectors.toSet());
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RowResult {
        private List<Map> data;
    }

}
