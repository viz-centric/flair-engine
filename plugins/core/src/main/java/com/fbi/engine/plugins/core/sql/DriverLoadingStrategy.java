package com.fbi.engine.plugins.core.sql;

import java.sql.Driver;

import com.fbi.engine.api.DataSourceDriver;

/**
 * Strategy used for dynamically loading drivers.
 */
public interface DriverLoadingStrategy {

	/**
	 * Load SQL driver based on the jar file.
	 * 
	 * @param driverClassName
	 * @param driver
	 * @throws error occurring when loading the driver
	 * @return
	 */
	Driver loadDriver(String driverClassName, DataSourceDriver driver) throws DriverLoadingException;

}
