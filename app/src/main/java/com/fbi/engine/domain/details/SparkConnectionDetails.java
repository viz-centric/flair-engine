package com.fbi.engine.domain.details;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Properties;

@Getter
@Setter
public class SparkConnectionDetails extends ConnectionDetails implements Serializable {

	private static final long serialVersionUID = 5326819193591278255L;

	public SparkConnectionDetails() {
	}

	public SparkConnectionDetails(String serverIp, Integer serverPort, String serviceName, String databaseName) {
		super(serverIp, serverPort, databaseName);
	}

	@Override
	public String getConnectionString() {
		StringBuilder connectionString = new StringBuilder();

		connectionString.append("jdbc:hive2:");
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
		return new Properties();
	}
}
