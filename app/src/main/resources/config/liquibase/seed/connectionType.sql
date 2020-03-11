INSERT INTO connection_type VALUES
(
   '1',
   'Postgres',
   'com.fbi.engine.query.factory.impl.PostgresFlairFactory',
   '{ "connectionDetailsType": "Postgres", "imagePath": "#postgresdb", "connectionDetailsClass": "com.fbi.engine.domain.details.PostgresConnectionDetails", "connectionProperties":[ { "displayName": "Server address", "fieldName": "serverIp", "order": 0, "fieldType": "String", "defaultValue": "localhost", "required": true }, { "displayName": "Port", "fieldName": "serverPort", "order" : 1, "fieldType": "Integer", "defaultValue": null, "required": true }, { "displayName": "Database name", "fieldName": "databaseName", "order": 2, "fieldType": "String", "defaultValue": null, "required": true }, { "displayName": "Connection params", "fieldName": "connectionParams", "order": 3, "fieldType": "String", "defaultValue": null, "required": false }] }'
);
INSERT INTO connection_type VALUES
(
   '2',
   'MySql',
   'com.fbi.engine.query.factory.impl.MySqlFlairFactory',
   '{ "connectionDetailsType": "MySql", "imagePath":"#mysqldb","connectionDetailsClass": "com.fbi.engine.domain.details.MySqlConnectionDetails", "connectionProperties":[ { "displayName": "Server address", "fieldName": "serverIp", "order": 0, "fieldType": "String", "defaultValue": "localhost", "required": true }, { "displayName": "Port", "fieldName": "serverPort", "order" : 1, "fieldType": "Integer", "defaultValue": null, "required": true }, { "displayName": "Database name", "fieldName": "databaseName", "order": 2, "fieldType": "String", "defaultValue": null, "required": true }] }'
);
INSERT INTO connection_type VALUES
(
   '3',
   'Oracle',
   'com.fbi.engine.query.factory.impl.OracleFlairFactory',
   '{ "connectionDetailsType": "Oracle", "imagePath":"#oracledb","connectionDetailsClass": "com.fbi.engine.domain.details.OracleConnectionDetails", "connectionProperties":[ { "displayName": "Server address", "fieldName": "serverIp", "order": 0, "fieldType": "String", "defaultValue": "localhost", "required": true }, { "displayName": "Port", "fieldName": "serverPort", "order" : 1, "fieldType": "Integer", "defaultValue": null, "required": true }, { "displayName": "Service name", "fieldName": "serviceName", "order": 2, "fieldType": "String", "defaultValue": null, "required": true }, { "displayName": "Database name", "fieldName": "databaseName", "order": 3, "fieldType": "String", "defaultValue": null, "required": true }] }'
);
INSERT INTO connection_type VALUES
(
   '4',
   'Spark',
   'com.fbi.engine.query.factory.impl.SparkFlairFactory',
   '{ "connectionDetailsType": "Spark", "imagePath":"#spark","connectionDetailsClass": "com.fbi.engine.domain.details.SparkConnectionDetails", "connectionProperties":[ { "displayName": "Server address", "fieldName": "serverIp", "order": 0, "fieldType": "String", "defaultValue": "localhost", "required": true }, { "displayName": "Port", "fieldName": "serverPort", "order" : 1, "fieldType": "Integer", "defaultValue": null, "required": true }, { "displayName": "Database name", "fieldName": "databaseName", "order": 3, "fieldType": "String", "defaultValue": null, "required": true }] }'
);
INSERT INTO connection_type VALUES
(
   '5',
   'MongoDB',
   'com.fbi.engine.query.factory.impl.MongoDBFlairFactory',
   '{ "connectionDetailsType": "MongoDB", "imagePath":"#mongodb","connectionDetailsClass": "com.fbi.engine.domain.details.MongoDBConnectionDetails", "connectionProperties":[ { "displayName": "Server address", "fieldName": "serverIp", "order": 0, "fieldType": "String", "defaultValue": "localhost", "required": true }, { "displayName": "Port", "fieldName": "serverPort", "order" : 1, "fieldType": "Integer", "defaultValue": null, "required": true }, { "displayName": "Service name", "fieldName": "serviceName", "order": 2, "fieldType": "String", "defaultValue": null, "required": true }, { "displayName": "Database name", "fieldName": "databaseName", "order": 3, "fieldType": "String", "defaultValue": null, "required": true }] }'
);
INSERT INTO connection_type VALUES
(
   '6',
   'Cockroachdb',
   'com.fbi.engine.query.factory.impl.CockroachdbFlairFactory',
   '{ "connectionDetailsType": "Cockroachdb", "imagePath": "#cockroach-lab", "connectionDetailsClass": "com.fbi.engine.domain.details.CockroachdbConnectionDetails", "connectionProperties":[ { "displayName": "Server address", "fieldName": "serverIp", "order": 0, "fieldType": "String", "defaultValue": "localhost", "required": true }, { "displayName": "Port", "fieldName": "serverPort", "order" : 1, "fieldType": "Integer", "defaultValue": null, "required": true }, { "displayName": "Database name", "fieldName": "databaseName", "order": 2, "fieldType": "String", "defaultValue": null, "required": true }, { "displayName": "Connection params", "fieldName": "connectionParams", "order": 3, "fieldType": "String", "defaultValue": null, "required": false }] }'
);
INSERT INTO connection_type VALUES
(
   '7',
   'Redshift',
   'com.fbi.engine.query.factory.impl.RedshiftFlairFactory',
   '{ "connectionDetailsType": "Redshift", "imagePath": "#aws-redshift", "connectionDetailsClass": "com.fbi.engine.domain.details.RedshiftConnectionDetails", "connectionProperties":[ { "displayName": "Server address", "fieldName": "serverIp", "order": 0, "fieldType": "String", "defaultValue": "localhost", "required": true }, { "displayName": "Port", "fieldName": "serverPort", "order" : 1, "fieldType": "Integer", "defaultValue": null, "required": true }, { "displayName": "Database name", "fieldName": "databaseName", "order": 2, "fieldType": "String", "defaultValue": null, "required": true }] }'
);
INSERT INTO connection_type VALUES
(
   '8',
   'Athena',
   'com.fbi.engine.query.factory.impl.AthenaFlairFactory',
   '{ "connectionDetailsType": "Athena", "imagePath": "#athena-s3", "connectionDetailsClass": "com.fbi.engine.domain.details.AthenaConnectionDetails", "connectionProperties":[ { "displayName": "Server address", "fieldName": "serverIp", "order": 0, "fieldType": "String", "defaultValue": "localhost", "required": true }, { "displayName": "Port", "fieldName": "serverPort", "order" : 1, "fieldType": "Integer", "defaultValue": null, "required": true }, { "displayName": "Database name", "fieldName": "databaseName", "order": 2, "fieldType": "String", "defaultValue": null, "required": true }, { "displayName": "S3 Output Location", "fieldName": "s3OutputLocation", "order": 3, "fieldType": "String", "defaultValue": null, "required": true }, { "displayName": "Work group", "fieldName": "workgroup", "order": 4, "fieldType": "String", "defaultValue": "primary", "required": true }] }'
);
INSERT INTO connection_type VALUES
(
   '9',
   'Kafka',
   'com.fbi.engine.query.factory.impl.KafkaFlairFactory',
   '{ "connectionDetailsType": "Kafka", "imagePath": "#kafka", "connectionDetailsClass": "com.fbi.engine.domain.details.KafkaConnectionDetails", "connectionProperties":[ { "displayName": "Server address", "fieldName": "serverIp", "order": 0, "fieldType": "String", "defaultValue": "localhost", "required": true }, { "displayName": "Port", "fieldName": "serverPort", "order" : 1, "fieldType": "Integer", "defaultValue": 8088, "required": true }, { "displayName": "Is secure", "fieldName": "isSecure", "order" : 2, "fieldType": "Boolean", "defaultValue": false, "required": false }] }'
);
INSERT INTO connection_type VALUES
(
   '10',
   'Snowflake',
   'com.fbi.engine.query.factory.impl.SnowflakeFlairFactory',
   '{ "connectionDetailsType": "Snowflake", "imagePath": "#snowflake", "connectionDetailsClass": "com.fbi.engine.domain.details.SnowflakeConnectionDetails", "connectionProperties":[ { "displayName": "Account name", "fieldName": "account", "order": 0, "fieldType": "String", "defaultValue": "mycompany", "required": true }, { "displayName": "Database name", "fieldName": "databaseName", "order": 1, "fieldType": "String", "required": true }, { "displayName": "Additional parameters", "fieldName": "additionalParameters", "order": 3, "fieldType": "String", "required": false }, { "displayName": "Schema name", "fieldName": "schemaName", "order": 2, "fieldType": "String", "defaultValue": "public", "required": true }] }'
);