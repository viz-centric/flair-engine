package com.fbi.engine.domain.query;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GenericQuery implements Query {

    private final String query;
    private final boolean pullMeta;

    @Override
    public boolean isMetadataRetrieved() {
        return pullMeta;
    }

    @Override
    public String getQuery() {
        return query;
    }
}
