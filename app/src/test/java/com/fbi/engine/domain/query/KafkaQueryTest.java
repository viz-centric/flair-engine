package com.fbi.engine.domain.query;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.project.bi.query.FlairQuery;

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
