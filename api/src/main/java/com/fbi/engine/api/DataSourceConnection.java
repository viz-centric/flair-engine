package com.fbi.engine.api;

import java.util.Properties;

/**
 * Connection information for data source.
 */
public interface DataSourceConnection {

	/**
	 * Connection string used for connecting to data source.
	 * 
	 * @return connection string
	 */
	String getConnectionString();

	/**
	 * Additional connection properties.
	 * 
	 * @return map with properties
	 */
	Properties getConnectionProperties();

}
