package com.fbi.engine.query.abstractfactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.pf4j.spring.SpringPluginManager;

import com.fbi.engine.api.FlairFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FlyweightQueryAbstractFactory implements QueryAbstractFactory {

	private final Map<String, FlairFactory> queryFactoryMap = new ConcurrentHashMap<>();

	private final SpringPluginManager springPluginManager;

	public FlyweightQueryAbstractFactory(SpringPluginManager springPluginManager) {
		super();
		this.springPluginManager = springPluginManager;
	}

	@PostConstruct
	public void init() {
		final List<FlairFactory> plugins = springPluginManager.getExtensions(FlairFactory.class);
		plugins.forEach(x -> {
			queryFactoryMap.put(x.getExtensionId(), x);
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public FlairFactory getQueryFactory(String name) {
		return queryFactoryMap.computeIfAbsent(name, x -> {
			try {
				Class<?> clazz = Class.forName(x);

				Class<? extends FlairFactory> factory = (Class<? extends FlairFactory>) clazz;

				return factory.newInstance();
			} catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
				log.error("Cannot determine type of factory: {}", e.getMessage());
				throw new RuntimeException("Cannot determine type of factory");
			}
		});

	}

}
