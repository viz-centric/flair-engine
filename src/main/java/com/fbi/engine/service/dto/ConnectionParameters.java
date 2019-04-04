package com.fbi.engine.service.dto;

import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class ConnectionParameters {

    private final Map<Object, Object> parameters;

    public boolean isEnabled() {
        return "true".equals(parameters.get("cacheEnabled"));
    }

    public int getCachePurgeAfterMinutes() {
        return Optional.ofNullable(parameters.get("cachePurgeAfterMinutes"))
                .map(i -> Integer.parseInt(String.valueOf(i)))
                .orElse(0);
    }

    public int getRefreshAfterTimesRead() {
        return Optional.ofNullable(parameters.get("refreshAfterTimesRead"))
                .map(i -> Integer.parseInt(String.valueOf(i)))
                .orElse(0);
    }

    public int getRefreshAfterMinutes() {
        return Optional.ofNullable(parameters.get("refreshAfterMinutes"))
                .map(i -> Integer.parseInt(String.valueOf(i)))
                .orElse(0);
    }
}
