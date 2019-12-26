package com.fbi.engine.domain.details;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Properties;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RedshiftConnectionDetails extends ConnectionDetails implements Serializable {

	private static final long serialVersionUID = 6314225956397785865L;

	public RedshiftConnectionDetails(String serverIp, Integer serverPort, String databaseName) {
		super(serverIp, serverPort, databaseName);
	}

	@Override
	public String getConnectionString() {
		StringBuilder connectionString = new StringBuilder();

		connectionString.append("jdbc:redshift:");

		connectionString.append("//").append(getServerIp());

		if (getServerPort() != null) {
			connectionString.append(":").append(getServerPort());
		}

		connectionString.append("/");
		connectionString.append(getDatabaseName());

		return connectionString.toString();
	}

	@Override
	public Properties getAdditionalProperties() {
		return new Properties();
	}
}
