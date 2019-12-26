package com.fbi.engine.plugins.test;

import java.io.StringWriter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import com.fbi.engine.api.Query;
import com.fbi.engine.api.QueryExecutor;
import com.project.bi.exceptions.ExecutionException;

public abstract class AbstractQueryExecutorUnitTest<T extends QueryExecutor> {
	@Rule
	public GenericContainer<?> container = configureTargetDataSource();

	protected T sut;

	protected abstract T configureQueryExecutor();

	protected abstract T misconfigureQueryExecutor();

	protected abstract GenericContainer<?> configureTargetDataSource();

	@Before
	public void before() {
		sut = configureQueryExecutor();
	}

	@Test
	public void testConnectionIsWorking() throws Exception {
		final StringWriter writer = new StringWriter();
		sut.execute(new Query() {

			@Override
			public boolean isMetadataRetrieved() {
				return false;
			}

			@Override
			public String getQuery() {
				return testConnection();
			}
		}, writer);
		writer.close();
	}

	protected String testConnection() {
		return "select 1";
	}

	protected String testQuery() {
		return "select * from transactions";
	}

	protected String testQueryFail() {
		return "select * from notexists";
	}

	@Test
	public void testQueryIsWorking() throws Exception {
		final StringWriter writer = new StringWriter();
		sut.execute(new Query() {

			@Override
			public boolean isMetadataRetrieved() {
				return false;
			}

			@Override
			public String getQuery() {
				return testQuery();
			}
		}, writer);
		writer.close();
	}

	@Test(expected = ExecutionException.class)
	public void testQueryNotWorking() throws Exception {
		final StringWriter writer = new StringWriter();
		sut.execute(new Query() {

			@Override
			public boolean isMetadataRetrieved() {
				return false;
			}

			@Override
			public String getQuery() {
				return testQueryFail();
			}
		}, writer);
		writer.close();
	}

	@Test(expected = ExecutionException.class)
	public void testConnectionNotWorking() throws Exception {
		sut = misconfigureQueryExecutor();
		final StringWriter writer = new StringWriter();
		sut.execute(new Query() {

			@Override
			public boolean isMetadataRetrieved() {
				return false;
			}

			@Override
			public String getQuery() {
				return testConnection();
			}
		}, writer);
		writer.close();
	}

}
