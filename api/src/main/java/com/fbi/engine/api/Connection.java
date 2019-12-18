package com.fbi.engine.api;

import java.util.Map;

/**
 * Connection information for data source.
 */
public interface Connection {

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
	Map<String, Object> getConnectionProperties();

}
