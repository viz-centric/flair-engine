package com.fbi.engine.api;

import org.pf4j.ExtensionPoint;

import com.project.bi.query.FlairCompiler;

/**
 * 
 * Factory used for creating execution environment for given data source.
 * 
 * @see FlairCompiler
 * @see QueryExecutor
 * @see Connection
 * @see DataSourceDriver
 */
public interface FlairFactory extends ExtensionPoint {

	/**
	 * Instantiate a compiler for given data source
	 *
	 * @return instance of {@link FlairCompiler}
	 */
	FlairCompiler getCompiler();

	/**
	 * Instantiate a executor for a given data source
	 *
	 * @param connection connection to given data source
	 * @param driver     used for creating the connection
	 * @return instance of {@link QueryExecutor}
	 */
	QueryExecutor getExecutor(Connection connection, DataSourceDriver driver);

}
