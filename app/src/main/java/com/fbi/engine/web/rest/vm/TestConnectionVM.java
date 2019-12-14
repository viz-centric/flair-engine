package com.fbi.engine.web.rest.vm;

import com.fbi.engine.domain.Connection;

import javax.validation.constraints.NotNull;

public class TestConnectionVM {

    private String connectionName;

    private Connection connection;

    @NotNull
    private String source;

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
