package com.fbi.engine.domain.details;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Slf4j
@Data
public class AthenaConnectionDetails extends ConnectionDetails implements Serializable {

    private String s3OutputLocation;
    private String workgroup;

    public AthenaConnectionDetails(String serverIp, Integer serverPort, String databaseName, String s3OutputLocation, String workgroup) {
        super(serverIp, serverPort, databaseName);
        this.s3OutputLocation = s3OutputLocation;
        this.workgroup = workgroup;
    }

    @Override
    public String getConnectionString() {
        StringBuilder connectionString = new StringBuilder();

        connectionString.append("jdbc:awsathena:");

        connectionString.append("//").append(getServerIp());

        if (getServerPort() != null) {
            connectionString.append(":").append(getServerPort());
        }

        return connectionString.toString();
    }
}
