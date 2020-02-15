package com.fbi.engine.domain.details;

import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Properties;

@EqualsAndHashCode(callSuper = true)
public class MySqlConnectionDetails extends ConnectionDetails implements Serializable {

	private static final long serialVersionUID = 423203250887764135L;

	public MySqlConnectionDetails() {
	}

	public MySqlConnectionDetails(String serverIp, Integer serverPort, String databaseName) {
		super(serverIp, serverPort, databaseName);
	}

	/**
	 * @return connection of mysql jdbc
	 */
	@Override
	public String getConnectionString() {
		StringBuilder connectionString = new StringBuilder();

		connectionString.append("jdbc:mysql:");

		if (getServerIp() != null) {
			connectionString.append("//").append(getServerIp());

			if (getServerPort() != null) {
				connectionString.append(":").append(getServerPort());
			}

			connectionString.append("/");
		}

		connectionString.append(getDatabaseName());
		connectionString.append("?useSSL=false");
		return connectionString.toString();
	}

	@Override
	public Properties getAdditionalProperties() {
		return new Properties();
	}
}
