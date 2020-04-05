package com.fbi.engine.query.executor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.domain.Connection;
import com.fbi.engine.domain.details.AthenaConnectionDetails;
import com.fbi.engine.domain.query.Query;
import com.project.bi.exceptions.ExecutionException;
import lombok.extern.slf4j.Slf4j;

import java.io.Writer;
import java.util.Properties;

@Slf4j
public class AthenaQueryExecutor extends SqlQueryExecutor {

    public AthenaQueryExecutor(Connection connection, ObjectMapper objectMapper) {
        super(connection, objectMapper);
    }

    @Override
    public void execute(Query query, Writer writer) throws ExecutionException {
        invokeQuery(query, writer);
    }

    @Override
    protected Properties getConnectionProperties() {
        Properties info = new Properties();
        if (this.connection.getConnectionUsername() != null) {
            info.put("user", this.connection.getConnectionUsername());
        }
        if (this.connection.getConnectionPassword() != null) {
            info.put("password", this.connection.getConnectionPassword());
        }

        AthenaConnectionDetails details = (AthenaConnectionDetails) this.connection.getDetails();
        info.put("S3OutputLocation", details.getS3OutputLocation());
        info.put("Workgroup", details.getWorkgroup());
        info.put("Schema", details.getDatabaseName());
        return info;
    }
}
