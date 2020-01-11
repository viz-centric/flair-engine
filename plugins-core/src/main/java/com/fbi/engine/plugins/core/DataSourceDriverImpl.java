package com.fbi.engine.plugins.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.fbi.engine.api.DataSourceDriver;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
public class DataSourceDriverImpl implements DataSourceDriver {

	private byte[] jar;
	private String artifactId;
	private String groupId;
	private String version;

	public static DataSourceDriverImpl of(File file, String artifactId, String groupId, String version) {
		try {
			return new DataSourceDriverImpl(Files.readAllBytes(file.toPath()), artifactId, groupId, version);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
