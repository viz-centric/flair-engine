package com.fbi.engine.plugins.hello;

import com.fbi.engine.api.DataSourceConnection;
import com.fbi.engine.api.DataSourceDriver;
import com.fbi.engine.api.QueryExecutor;

public interface QueryExecutorFactory {

	QueryExecutor createQueryExecutor(DataSourceConnection connection, DataSourceDriver driver);

}
