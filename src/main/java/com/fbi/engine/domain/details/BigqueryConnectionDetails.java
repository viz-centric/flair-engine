package com.fbi.engine.domain.details;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BigqueryConnectionDetails extends ConnectionDetails implements Serializable {

    private String privateKey;
    private String privateKeyPath;
    private String email;
    private String projectId;

    @Override
    public String getConnectionString() {
        StringBuilder connectionString = new StringBuilder();

        connectionString.append("jdbc:bigquery://https://www.googleapis.com/bigquery/v2:443")
                .append(";DefaultDataset=")
                .append(getDatabaseName())
                .append(";OAuthServiceAcctEmail=")
                .append(email)
                .append(";OAuthType=0")
                .append(";ProjectId=")
                .append(projectId)
                .append(";OAuthPvtKeyPath=")
                .append(privateKeyPath)
        ;

        return connectionString.toString();
    }

    @Override
    public boolean isExternal() {
        return true;
    }
}
