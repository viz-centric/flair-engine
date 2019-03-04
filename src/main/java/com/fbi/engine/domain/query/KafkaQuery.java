package com.fbi.engine.domain.query;

import com.project.bi.query.FlairQuery;

public class KafkaQuery implements Query {

    private String query;
    private final boolean pullMeta;
    private final String source;
    private final FlairQuery flairQuery;

    public KafkaQuery(String query, boolean pullMeta, String source, FlairQuery flairQuery) {
        this.query = query;
        this.pullMeta = pullMeta;
        this.source = source;
        this.flairQuery = flairQuery;
    }

    public FlairQuery getFlairQuery() {
        return flairQuery;
    }

    @Override
    public boolean isMetadataRetrieved() {
        return pullMeta;
    }

    public String getSource() {
        return source;
    }

    @Override
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
        this.flairQuery.setStatement(query);
    }
}
