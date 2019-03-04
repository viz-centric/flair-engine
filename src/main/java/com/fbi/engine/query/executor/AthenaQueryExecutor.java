package com.fbi.engine.query.executor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.domain.Connection;
import com.fbi.engine.domain.details.AthenaConnectionDetails;
import com.fbi.engine.domain.details.ConnectionDetails;
import com.fbi.engine.domain.query.Query;
import com.fbi.engine.query.convert.impl.ResultSetConverter;
import com.project.bi.exceptions.ExecutionException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Writer;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

@Slf4j
public class AthenaQueryExecutor extends SqlQueryExecutor {

    public AthenaQueryExecutor(Connection connection, ObjectMapper objectMapper) {
        super(connection, objectMapper);
    }

    @Override
    protected void loadDrivers() throws ClassNotFoundException {
        Class.forName("com.simba.athena.jdbc42.Driver");
    }

    @Override
    public void execute(Query query, Writer writer) throws ExecutionException {
        try {
            loadDrivers();
        } catch (ClassNotFoundException e) {
            log.error("Driver is not supported: {}", e.getMessage());
            throw new ExecutionException("Driver not supported", e);
        }

        Properties info = new Properties();
        if (this.connection.getConnectionUsername() != null) {
            info.put("user", this.connection.getConnectionUsername());
        }
        if (this.connection.getConnectionPassword() != null) {
            info.put("password", this.connection.getConnectionPassword());
        }

        AthenaConnectionDetails details = (AthenaConnectionDetails) this.connection.getDetails();
        info.put("S3OutputLocation", details.getS3OutputLocation());
        info.put("Schema", details.getDatabaseName());

        try (java.sql.Connection connection = DriverManager.getConnection(details.getConnectionString(), info)) {
            if (connection != null) {
                Statement statement = connection.createStatement();

                statement.execute(query.getQuery());
                ResultSet resultSet = statement.getResultSet();

                writer.write(new ResultSetConverter(objectMapper, query.isMetadataRetrieved()).convert(resultSet));

                statement.close();
                log.debug("Connection closed");
            } else {
                log.error("Failed to make connection!");
                throw new ExecutionException("Failed to create a connection");
            }

        } catch (SQLException e) {
            log.error("Connection to database failed, stacktrace: {}", e.getMessage());
            throw new ExecutionException("Database threw an exception", e);
        } catch (IOException e) {
            log.error("Reading data failed, message: {}", e.getMessage());
            throw new ExecutionException("Reading data failed", e);
        }
    }
}
