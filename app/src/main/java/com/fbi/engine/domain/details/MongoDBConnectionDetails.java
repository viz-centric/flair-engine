package com.fbi.engine.domain.details;

import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Properties;

@EqualsAndHashCode(callSuper = true)
public class MongoDBConnectionDetails extends ConnectionDetails implements Serializable {

	private static final long serialVersionUID = -3039039079896004839L;

	public MongoDBConnectionDetails() {
	}

	public MongoDBConnectionDetails(String serverIp, Integer serverPort, String databaseName) {
		super(serverIp, serverPort, databaseName);
	}

	/**
	 * @return connection of mongodb jdbc
	 * 
	 */
	@Override
	public String getConnectionString() {
		StringBuilder connectionString = new StringBuilder();

		connectionString.append("jdbc:mongo:");

		if (getServerIp() != null) {
			connectionString.append("//").append(getServerIp());

			if (getServerPort() != null) {
				connectionString.append(":").append(getServerPort());
			}

			connectionString.append("/");
		}

		connectionString.append(getDatabaseName());
		return connectionString.toString();
	}

	@Override
	public Properties getAdditionalProperties() {
		Properties props = new Properties();
		props.put("databaseName", getDatabaseName());
		props.put("serverPort", getServerPort());
		props.put("serverIp", getServerIp());
		return props;
	}
}
