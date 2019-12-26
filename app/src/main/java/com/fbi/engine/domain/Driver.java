package com.fbi.engine.domain;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import com.fbi.engine.api.DataSourceDriver;

import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * Driver being used for establishing a connection to the given data source.
 * 
 * @see Connection
 */
@Setter
@ToString(exclude = { "jar" })
@EqualsAndHashCode(of = { "artifactId", "groupId", "version" })
@Embeddable
public class Driver implements DataSourceDriver {

	@NotNull
	private byte[] jar;
	private String artifactId;
	private String groupId;
	private String version;

	@Override
	public byte[] getJar() {
		return jar;
	}

	@Override
	public String getArtifactId() {
		return artifactId;
	}

	@Override
	public String getGroupId() {
		return groupId;
	}

	@Override
	public String getVersion() {
		return version;
	}

}
