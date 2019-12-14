package com.fbi.engine.domain.details;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
public class MongoDBConnectionDetails extends ConnectionDetails implements Serializable {

    public MongoDBConnectionDetails() {
    }

    public MongoDBConnectionDetails(String serverIp, Integer serverPort, String databaseName) {
        super(serverIp, serverPort, databaseName);
    }

    /**
     * @return connection of mongodb jdbc
     * 
     */
    @Override
    public String getConnectionString() {
        StringBuilder connectionString = new StringBuilder();

        connectionString.append("jdbc:mongo:");

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
