package com.fbi.engine.plugins.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.apache.commons.io.IOUtils;

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

	/**
	 * Construct driver based on file loaded from file system.
	 * 
	 * @param file
	 * @param artifactId
	 * @param groupId
	 * @param version
	 * @return driver
	 */
	public static DataSourceDriverImpl of(File file, String artifactId, String groupId, String version) {
		try {
			return new DataSourceDriverImpl(Files.readAllBytes(file.toPath()), artifactId, groupId, version);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Construct driver based on class path resource.
	 * 
	 * @param file
	 * @param artifactId
	 * @param groupId
	 * @param version
	 * @return driver
	 */
	public static DataSourceDriverImpl of(String file, String artifactId, String groupId, String version) {
		try (final InputStream is = DataSourceDriverImpl.class.getClassLoader().getResourceAsStream(file)) {
			return DataSourceDriverImpl.of(is, artifactId, groupId, version);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Construct driver based on input stream.
	 * 
	 * @param stream
	 * @param artifactId
	 * @param groupId
	 * @param version
	 * @return
	 */
	public static DataSourceDriverImpl of(InputStream stream, String artifactId, String groupId, String version) {
		try {
			byte[] content = IOUtils.toByteArray(stream);
			return new DataSourceDriverImpl(content, artifactId, groupId, version);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
