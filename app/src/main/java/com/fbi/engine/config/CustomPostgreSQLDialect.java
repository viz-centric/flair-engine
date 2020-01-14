package com.fbi.engine.config;

import java.sql.Types;

import org.hibernate.dialect.PostgreSQL82Dialect;
import org.hibernate.type.descriptor.sql.BinaryTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

public class CustomPostgreSQLDialect extends PostgreSQL82Dialect {

	public CustomPostgreSQLDialect() {
		super();
		registerColumnType(Types.BLOB, "bytea");
	}

	@Override
	public SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
		if (sqlTypeDescriptor.getSqlType() == Types.BLOB) {
			return BinaryTypeDescriptor.INSTANCE;
		}
		return super.remapSqlTypeDescriptor(sqlTypeDescriptor);
	}

}
