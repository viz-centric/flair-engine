package com.fbi.engine.driver;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DriverLoadService {

    public DriverLoadService(DriversConfig config) throws Exception {
        log.info("Loading drivers");

        String driversDir = config.getDir();
        if (StringUtils.isEmpty(driversDir)) {
            return;
        }

        List<URL> urls = Files.list(Paths.get(driversDir))
                .filter(p -> p.toFile().getName().endsWith(".jar"))
                .map(p -> "file:" + p.toAbsolutePath())
                .map(p -> {
                    try {
                        return new URL(p);
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        List<String> driversNames = config.getNames();

        for (String classname : driversNames) {
            URLClassLoader ucl = new URLClassLoader(urls.toArray(new URL[]{}));
            Driver d = (Driver) Class.forName(classname, true, ucl).newInstance();
            DriverManager.registerDriver(new DriverShim(d));
        }
    }
}
