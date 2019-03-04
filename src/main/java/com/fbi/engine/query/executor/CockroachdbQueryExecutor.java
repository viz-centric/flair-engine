package com.fbi.engine.query.executor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.domain.Connection;

public class CockroachdbQueryExecutor extends PostgresSqlQueryExecutor {

    public CockroachdbQueryExecutor(Connection connection, ObjectMapper objectMapper) {
        super(connection, objectMapper);
    }
}
