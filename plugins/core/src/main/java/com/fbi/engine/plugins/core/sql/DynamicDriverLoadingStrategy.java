package com.fbi.engine.plugins.core.sql;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Driver;

import com.fbi.engine.api.DataSourceDriver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DynamicDriverLoadingStrategy implements DriverLoadingStrategy {

	@Override
	public Driver loadDriver(String classname, DataSourceDriver driver) throws DriverLoadingException {
		try {
			final File file = load(driver.getJar());
			URLClassLoader classLoader = new URLClassLoader(new URL[] { file.toURI().toURL() },
					ClassLoader.getSystemClassLoader());
			Driver d = (Driver) Class.forName(classname, true, classLoader).newInstance();
			return d;
		} catch (MalformedURLException e) {
			log.error("An error occured resolving url for the jar file");
			throw new DriverLoadingException(e);
		} catch (IOException e) {
			log.error("An error occured trying to load the jar file");
			throw new DriverLoadingException(e);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			log.error("Error occured instantiating driver: {}", classname);
			throw new DriverLoadingException(e);
		}
	}

	private static File load(byte[] jarBytes) throws IOException {
		// Create my temporary file
		Path path = Files.createTempFile("tempJarFile", "jar");
		// Delete the file on exit
		path.toFile().deleteOnExit();
		// Copy the content of my jar into the temporary file
		try (InputStream is = new ByteArrayInputStream(jarBytes)) {
			Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
		}
		return path.toFile();
	}

}
