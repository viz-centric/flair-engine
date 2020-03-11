package com.fbi.engine.domain.details;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CockroachdbConnectionDetails extends PostgresConnectionDetails {

	private static final long serialVersionUID = 4380681452909463305L;

	public CockroachdbConnectionDetails(String serverIp, Integer serverPort, String databaseName, String connectionParams) {
		super(serverIp, serverPort, databaseName, connectionParams);
	}

}
