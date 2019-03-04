package com.fbi.engine.domain.details;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SparkConnectionDetails extends ConnectionDetails implements Serializable {

    private String serviceName;

    public SparkConnectionDetails() {
    }

    public SparkConnectionDetails(String serverIp, Integer serverPort, String serviceName, String databaseName) {
        super(serverIp, serverPort, databaseName);
        this.serviceName = serviceName;
    }

    @Override
    public String getConnectionString() {
        StringBuilder connectionString = new StringBuilder();

        connectionString.append("http:");
        if (getServerIp() != null) {
            connectionString.append("//").append(getServerIp());

            if (getServerPort() != null) {
                connectionString.append(":").append(getServerPort());
            }

            connectionString.append("/");
        }

        connectionString.append(getServiceName());
        return connectionString.toString();
    }
}
