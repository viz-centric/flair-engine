package com.fbi.engine.api;

import org.pf4j.ExtensionPoint;

public interface FlairExtensionPoint extends ExtensionPoint {

	default String getExtensionId() {
		return getClass().getName();
	}

	default String getDescription() {
		return "";
	}

}
