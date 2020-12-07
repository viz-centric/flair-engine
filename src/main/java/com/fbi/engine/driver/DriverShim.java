package com.fbi.engine.driver;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class DriverShim implements Driver {
    private final Driver driver;
    public DriverShim(Driver d) {
        this.driver = d;
    }
    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        return this.driver.connect(url, info);
    }
    @Override
    public boolean acceptsURL(String u) throws SQLException {
        return this.driver.acceptsURL(u);
    }
    @Override
    public int getMajorVersion() {
        return this.driver.getMajorVersion();
    }
    @Override
    public int getMinorVersion() {
        return this.driver.getMinorVersion();
    }
    @Override
    public DriverPropertyInfo[] getPropertyInfo(String u, Properties p) throws SQLException {
        return this.driver.getPropertyInfo(u, p);
    }
    @Override
    public boolean jdbcCompliant() {
        return this.driver.jdbcCompliant();
    }
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return this.driver.getParentLogger();
    }

}
