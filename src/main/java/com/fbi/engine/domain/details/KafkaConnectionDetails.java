package com.fbi.engine.domain.details;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang.BooleanUtils;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class KafkaConnectionDetails extends ConnectionDetails implements Serializable {

    private Boolean isSecure;

    public KafkaConnectionDetails() {
    }

    public KafkaConnectionDetails(String serverIp, int serverPort, String databaseName, boolean isSecure) {
        super(serverIp, serverPort, databaseName);
        this.isSecure = isSecure;
    }

    @Override
    public String getConnectionString() {
        StringBuilder connectionString = new StringBuilder();

        if (BooleanUtils.toBoolean(isSecure)) {
            connectionString.append("https");
        } else {
            connectionString.append("http");
        }

        connectionString
            .append("://")
            .append(getServerIp())
            .append(":")
            .append(getServerPort());

        return connectionString.toString();
    }
}
