package com.fbi.engine.domain.details;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class SnowflakeConnectionDetails extends ConnectionDetails implements Serializable {

    private String account;
    private String additionalParameters;
    private String schemaName;

    public SnowflakeConnectionDetails() {
    }

    public SnowflakeConnectionDetails(String account, String databaseName, String additionalParameters, String schemaName) {
        super(null, null, databaseName);
        this.account = account;
        this.additionalParameters = additionalParameters;
        this.schemaName = schemaName;
    }

    //jdbc driver class
    //com.snowflake.client.jdbc.SnowflakeDriver
    // jdbc:snowflake://<account_name>.snowflakecomputing.com/?<connection_params>
    @Override
    public String getConnectionString() {
        StringBuilder connectionString = new StringBuilder();

        connectionString.append("jdbc:snowflake:")
                .append("//")
                .append(getAccount())
                .append(".snowflakecomputing.com")
                .append("/")
                .append("?db=")
                .append(getDatabaseName())
                .append("&CLIENT_SESSION_KEEP_ALIVE=true")
                .append("&schema=")
                .append(getSchemaName())
        ;

        if (additionalParameters != null) {
            connectionString.append("&")
                    .append(additionalParameters);
        }

        return connectionString.toString();
    }
}
