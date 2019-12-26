package com.fbi.engine.query.abstractfactory;

import com.fbi.engine.api.FlairFactory;
import com.project.bi.general.AbstractFactory;

public interface QueryAbstractFactory extends AbstractFactory {

	FlairFactory getQueryFactory(String clazz);

	@Override
	default String getAbstractFactoryName() {
		return getClass().getName();
	}
}
