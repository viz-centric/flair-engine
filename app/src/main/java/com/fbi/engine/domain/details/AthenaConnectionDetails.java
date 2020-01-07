package com.fbi.engine.domain.details;

import java.io.Serializable;
import java.util.Properties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class AthenaConnectionDetails extends ConnectionDetails implements Serializable {

	private static final long serialVersionUID = -8826789011396868900L;
	private String s3OutputLocation;
	private String workgroup;

	public AthenaConnectionDetails(String serverIp, Integer serverPort, String databaseName, String s3OutputLocation,
			String workgroup) {
		super(serverIp, serverPort, databaseName);
		this.s3OutputLocation = s3OutputLocation;
		this.workgroup = workgroup;
	}

	@Override
	public String getConnectionString() {
		StringBuilder connectionString = new StringBuilder();

		connectionString.append("jdbc:awsathena:");

		connectionString.append("//").append(getServerIp());

		if (getServerPort() != null) {
			connectionString.append(":").append(getServerPort());
		}

		return connectionString.toString();
	}

	@Override
	public Properties getAdditionalProperties() {
		Properties info = new Properties();
		info.put("S3OutputLocation", this.getS3OutputLocation());
		info.put("Workgroup", this.getWorkgroup());
		info.put("Schema", this.getDatabaseName());
		return info;
	}
}
