package com.fbi.engine.domain.query;

import com.project.bi.query.FlairQuery;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class KafkaQueryTest {

    @Test
    public void setQuery() {
        FlairQuery flairQuery = new FlairQuery("statement", false);
        KafkaQuery query = new KafkaQuery("select 1", true, "tablename", flairQuery);
        query.setQuery("select * from table limit 1");

        assertEquals("select * from table limit 1", query.getQuery());
        assertEquals("select * from table limit 1", query.getFlairQuery().getStatement());
    }
}
