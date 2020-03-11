package com.fbi.engine.domain.details;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Properties;
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PostgresConnectionDetails extends ConnectionDetails implements Serializable {

	private static final long serialVersionUID = -3202396803334508134L;

	private String connectionParams;


	public PostgresConnectionDetails(String serverIp, Integer serverPort, String databaseName, String connectionParams) {
		super(serverIp, serverPort, databaseName);
		this.connectionParams = connectionParams;
	}

	/**
	 * Connection string created based on following document:
	 * <p>
	 * https://jdbc.postgresql.org/documentation/80/connect.html
	 *
	 * @return connection string for Postgres database.
	 */
	@Override
	public String getConnectionString() {
		StringBuilder connectionString = new StringBuilder();

		connectionString.append("jdbc:postgresql:");

		if (getServerIp() != null) {
			connectionString.append("//").append(getServerIp());

			if (getServerPort() != null) {
				connectionString.append(":").append(getServerPort());
			}

			connectionString.append("/");
		}
		connectionString.append(getDatabaseName());

		if (getConnectionParams() != null) {
            connectionString.append("?")
                    .append(getConnectionParams());
        }

		return connectionString.toString();
	}

	@Override
	public Properties getAdditionalProperties() {
		return new Properties();
	}
}
