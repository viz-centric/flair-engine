package com.fbi.engine.api;

/**
 * Query used for retrieving data from data source.
 */
public interface Query {

	/**
	 * Boolean representing if metadata should be retrieved.
	 * 
	 * @return true, otherwise false
	 */
	boolean isMetadataRetrieved();

	/**
	 * String representing custom query.
	 * 
	 * @return query as string representation
	 */
	String getQuery();
}
