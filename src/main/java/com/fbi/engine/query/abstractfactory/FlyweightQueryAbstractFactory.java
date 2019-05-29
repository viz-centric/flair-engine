package com.fbi.engine.query.abstractfactory;

import com.fbi.engine.query.factory.FlairFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class FlyweightQueryAbstractFactory implements QueryAbstractFactory {

    private volatile Map<String, FlairFactory> queryFactoryMap = new ConcurrentHashMap<>();


    @Override
    public FlairFactory getQueryFactory(String name) {
        return queryFactoryMap.computeIfAbsent(name, x -> {
            Class<? extends FlairFactory> factory;
            try {
                Class clazz = Class.forName(x);

                if (FlairFactory.class.isAssignableFrom(clazz)) {
                    factory = (Class<? extends FlairFactory>) clazz;
                } else {
                    throw new ClassNotFoundException();
                }
                return factory.newInstance();
            } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                log.error("Cannot determine type of factory: {}", e.getMessage());
                throw new RuntimeException("Cannot determine type of factory");
            }
        });

    }


}
