package com.fbi.engine.service.database;

import agent.Agent;
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
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

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
        List<File> files = loadDriverFiles();
        log.info("Found drivers {}", files);

        files.forEach((file) -> {
            try {
                Agent.appendJarFile(new JarFile(file));
            } catch (IOException e) {
                log.error("Error appending jar " + file, e);
            }
        });

        registerDrivers(driverMapping, null);

////        URLClassLoader sysLoader = URLClassLoader.newInstance(files.toArray(new URL[]{}), ClassLoader.getSystemClassLoader());
////        URLClassLoader sysLoader = new URLClassLoader(new URL[0]);
//
//        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
////        if (!(systemClassLoader instanceof URLClassLoader)) {
////            log.info("System class loader");
////            return;
////        }
//        ClassLoader sysLoader = systemClassLoader;
//        Method sysMethod;
//        try {
//            sysMethod = sysLoader.getClass().getDeclaredMethod("addURL", URL.class);
//        } catch (NoSuchMethodException e) {
//            log.info("Not found addUrl via reflection", e);
//            sysMethod = sysLoader.getClass()
//                    .getDeclaredMethod("appendToClassPathForInstrumentation", String.class);
//        }
////        Method sysMethod = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
//        sysMethod.setAccessible(true);
//
//        Method finalSysMethod = sysMethod;
//        files.forEach((url) -> loadUrl(sysLoader, finalSysMethod, url));
//
//        log.info("Loading drivers finished");
//
//        registerDrivers(driverMapping, sysLoader);

        log.info("Registering drivers finished");
    }

    private void registerDrivers(Map<String, String> driverMapping, ClassLoader sysLoader) {
        driverMapping.forEach((key, value) -> {
            try {
                Driver dr;
                if (sysLoader == null) {
                    dr = (Driver) Class.forName(value).newInstance();
                } else {
                    dr = (Driver) Class.forName(value, true, sysLoader).newInstance();
                }
                log.info("Registering driver {} with {} = {}", key, value, dr);
                DriverManager.registerDriver(dr);
            } catch (Exception e) {
                log.error("Error doing class forName for driver " + key + "(" + value + ")", e);
            }
        });

        for(Enumeration<Driver> e = DriverManager.getDrivers(); e.hasMoreElements(); ) {
            log.info("Driver registered {}", e.nextElement());
        }
    }

    private List<File> loadDriverFiles() {
        File dir = new File(driversPath);
        File[] files = dir.listFiles(pathname -> pathname.isFile());
        return Arrays.asList(files);
    }

    private Map<String, String> loadJsonDriversMapping() throws IOException {
        File json = new File(driversPath, "drivers.json");
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, HashMap.class);
    }

    private void loadUrl(ClassLoader sysLoader, Method sysMethod, File url) {
        try {
            sysMethod.invoke(sysLoader, url.toURI().toURL());
        } catch (Exception e) {
            log.info("Invoke failed addUrl via reflection", e);
            try {
                sysMethod.invoke(sysLoader, url.getAbsolutePath());
            } catch (Exception ex) {
                throw new RuntimeException("Invoke failed from " + driversPath + " for url " + url, e);
            }
        }

//        try {
//            sysMethod.invoke(sysLoader, new Object[]{url});
//        } catch (Exception e) {
//            throw new RuntimeException("Cannot load database drivers from " + driversPath + " for url " + url, e);
//        }
    }

    private URL fileToUrl(File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Cannot create url from file " + file, e);
        }
    }
}
