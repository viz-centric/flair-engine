package com.fbi.engine.plugins.hello;

import com.fbi.engine.api.Connection;
import com.fbi.engine.api.DataSourceDriver;
import com.fbi.engine.api.QueryExecutor;

public interface QueryExecutorFactory {

	QueryExecutor createQueryExecutor(Connection connection, DataSourceDriver driver);

}
