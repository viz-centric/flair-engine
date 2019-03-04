package com.fbi.engine.domain.details;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class OracleConnectionDetails extends ConnectionDetails implements Serializable {

    private String serviceName;

    public OracleConnectionDetails() {
    }

    public OracleConnectionDetails(String serverIp, Integer serverPort, String serviceName, String databaseName) {
        super(serverIp, serverPort, databaseName);
        this.serviceName = serviceName;
    }

    @Override
    public String getConnectionString() {
        StringBuilder connectionString = new StringBuilder();

        connectionString.append("jdbc:oracle:thin");

        if (getServerIp() != null) {
            connectionString.append(":@//").append(getServerIp());

            if (getServerPort() != null) {
                connectionString.append(":").append(getServerPort());
            }

            connectionString.append("/");
        }

        connectionString.append(getServiceName());
        return connectionString.toString();
    }
}
