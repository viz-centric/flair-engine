package com.fbi.engine.query;

import com.fbi.engine.domain.Connection;
import com.project.bi.query.FlairQuery;

/**
 * Service responsible for communicating with other external data sources
 */
public interface QueryService {

    String executeQuery(Connection sources, FlairQuery query);
    
}
