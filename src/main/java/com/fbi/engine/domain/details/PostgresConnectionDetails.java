package com.fbi.engine.domain.details;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
public class PostgresConnectionDetails extends ConnectionDetails implements Serializable {

    public PostgresConnectionDetails() {
    }

    public PostgresConnectionDetails(String serverIp, Integer serverPort, String databaseName) {
        super(serverIp, serverPort, databaseName);
    }


    /**
     * Connection string created based on following document:
     * <p>
     * https://jdbc.postgresql.org/documentation/80/connect.html
     *
     * @return connection string for Postgres database.
     */
    @Override
    public String getConnectionString() {
        StringBuilder connectionString = new StringBuilder();

        connectionString.append("jdbc:postgresql:");

        if (getServerIp() != null) {
            connectionString.append("//").append(getServerIp());

            if (getServerPort() != null) {
                connectionString.append(":").append(getServerPort());
            }

            connectionString.append("/");
        }
        connectionString.append(getDatabaseName());

        return connectionString.toString();
    }
}
