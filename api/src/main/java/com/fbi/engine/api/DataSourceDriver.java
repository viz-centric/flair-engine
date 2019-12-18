package com.fbi.engine.api;

/**
 * Driver used for connecting to the data source.
 * 
 * @see Connection
 * @see FlairFactory
 */
public interface DataSourceDriver {

	/**
	 * Jar file containing the driver.
	 * 
	 * @return driver
	 */
	byte[] getJar();

	/**
	 * Artifact id of the driver.
	 * 
	 * @return artifact id
	 */
	String getArtifactId();

	/**
	 * Group id of the driver.
	 * 
	 * @return group id
	 */
	String getGroupId();

	/**
	 * Version of the driver.
	 * 
	 * @return version
	 */
	String getVersion();

}
