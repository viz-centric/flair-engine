package com.fbi.engine.service.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DatabaseDriverLoaderService {

    @Value("${drivers.path}")
    private String driversPath;

    @PostConstruct
    public void databaseDriverMap() throws Exception {
        log.info("Loading drivers json {}", driversPath);
        Map<String, String> driverMapping = loadJsonDriversMapping();

        log.info("Loading drivers from {}", driversPath);
        List<URL> urls = loadDriverFiles();
        log.info("Found drivers {}", urls);

        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        if (!(systemClassLoader instanceof URLClassLoader)) {
            return;
        }
        URLClassLoader sysLoader = (URLClassLoader) systemClassLoader;
        Method sysMethod = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
        sysMethod.setAccessible(true);

        urls.forEach((url) -> loadUrl(sysLoader, sysMethod, url));

        log.info("Loading drivers finished");

        registerDrivers(driverMapping, sysLoader);

        log.info("Registering drivers finished");
    }

    private void registerDrivers(Map<String, String> driverMapping, URLClassLoader sysLoader) {
        driverMapping.forEach((key, value) -> {
            try {
                Driver dr = (Driver) Class.forName(value, true, sysLoader).newInstance();
                DriverManager.registerDriver(dr);
            } catch (Exception e) {
                log.error("Error doing class forName for driver " + key + "(" + value + ")", e);
            }
        });

        for(Enumeration<Driver> e = DriverManager.getDrivers(); e.hasMoreElements(); ) {
            log.info("Driver registered {}", e.nextElement());
        }
    }

    private List<URL> loadDriverFiles() {
        File dir = new File(driversPath);
        File[] files = dir.listFiles(pathname -> pathname.isFile());
        return Arrays.stream(files)
                .map(file -> fileToUrl(file))
                .collect(Collectors.toList());
    }

    private Map<String, String> loadJsonDriversMapping() throws IOException {
        File json = new File(driversPath, "drivers.json");
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, HashMap.class);
    }

    private void loadUrl(URLClassLoader sysLoader, Method sysMethod, URL url) {
        try {
            sysMethod.invoke(sysLoader, new Object[]{url});
        } catch (Exception e) {
            throw new RuntimeException("Cannot load database drivers from " + driversPath + " for url " + url, e);
        }
    }

    private URL fileToUrl(File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Cannot create url from file " + file, e);
        }
    }
}
