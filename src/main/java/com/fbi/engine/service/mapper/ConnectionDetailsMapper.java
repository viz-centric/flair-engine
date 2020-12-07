package com.fbi.engine.service.mapper;

import com.fbi.engine.domain.details.AthenaConnectionDetails;
import com.fbi.engine.domain.details.BigqueryConnectionDetails;
import com.fbi.engine.domain.details.CockroachdbConnectionDetails;
import com.fbi.engine.domain.details.ConnectionDetails;
import com.fbi.engine.domain.details.KafkaConnectionDetails;
import com.fbi.engine.domain.details.MongoDBConnectionDetails;
import com.fbi.engine.domain.details.MySqlConnectionDetails;
import com.fbi.engine.domain.details.OracleConnectionDetails;
import com.fbi.engine.domain.details.PostgresConnectionDetails;
import com.fbi.engine.domain.details.RedshiftConnectionDetails;
import com.fbi.engine.domain.details.SnowflakeConnectionDetails;
import com.fbi.engine.domain.details.SparkConnectionDetails;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ConnectionDetailsMapper {

    public ConnectionDetails mapToEntity(Map<String, String> value) {
        ConnectionDetails connectionDetails;
        switch (value.get("@type")) {
            case "Oracle":
                connectionDetails = new OracleConnectionDetails();
                ((OracleConnectionDetails) connectionDetails).setServiceName(value.get("serviceName"));
                break;
            case "MySql":
                connectionDetails = new MySqlConnectionDetails();
                break;
            case "Postgres":
                connectionDetails = new PostgresConnectionDetails();
                ((PostgresConnectionDetails) connectionDetails).setConnectionParams(value.get("connectionParams"));
                break;
            case "Cockroachdb":
                connectionDetails = new CockroachdbConnectionDetails();
                ((CockroachdbConnectionDetails) connectionDetails).setConnectionParams(value.get("connectionParams"));
                break;
            case "Redshift":
                connectionDetails = new RedshiftConnectionDetails();
                break;
            case "Athena":
                connectionDetails = new AthenaConnectionDetails();
                ((AthenaConnectionDetails) connectionDetails).setS3OutputLocation(value.get("s3OutputLocation"));
                ((AthenaConnectionDetails) connectionDetails).setWorkgroup(value.get("workgroup"));
                break;
            case "Spark":
                connectionDetails = new SparkConnectionDetails();
                break;
            case "MongoDB":
                connectionDetails = new MongoDBConnectionDetails();
                break;
            case "Kafka":
                connectionDetails = new KafkaConnectionDetails();
                ((KafkaConnectionDetails) connectionDetails).setIsSecure(BooleanUtils.toBoolean(value.get("isSecure")));
                break;
            case "Snowflake":
                connectionDetails = new SnowflakeConnectionDetails();
                ((SnowflakeConnectionDetails) connectionDetails).setAccount(value.get("account"));
                if (value.get("additionalParameters") != null) {
                    ((SnowflakeConnectionDetails) connectionDetails).setAdditionalParameters(value.get("additionalParameters"));
                }
                ((SnowflakeConnectionDetails) connectionDetails).setSchemaName(value.get("schemaName"));
                break;
            case "BigQuery":
                connectionDetails = new BigqueryConnectionDetails();
                ((BigqueryConnectionDetails) connectionDetails).setEmail(value.get("email"));
                ((BigqueryConnectionDetails) connectionDetails).setPrivateKey(value.get("privateKey"));
                ((BigqueryConnectionDetails) connectionDetails).setProjectId(value.get("projectId"));
                ((BigqueryConnectionDetails) connectionDetails).setDatabaseName(value.get("dataset"));
                break;
            default:
                throw new RuntimeException("Cannot find a mapper for " + value);
        }
        if (value.get("databaseName") != null) {
            connectionDetails.setDatabaseName(value.get("databaseName"));
        }
        if (value.get("serverIp") != null) {
            connectionDetails.setServerIp(value.get("serverIp"));
        }
        if (value.get("serverPort") != null) {
            connectionDetails.setServerPort(Integer.parseInt(value.get("serverPort")));
        }
        return connectionDetails;
    }

    public Map<String, String> entityToMap(ConnectionDetails connectionDetails) {
        HashMap<String, String> map = new HashMap<>();
        if (connectionDetails instanceof OracleConnectionDetails) {
            map.put("@type", "Oracle");
            map.put("serviceName", ((OracleConnectionDetails) connectionDetails).getServiceName());
        } else if (connectionDetails instanceof MySqlConnectionDetails) {
            map.put("@type", "MySql");
        } else if (connectionDetails instanceof CockroachdbConnectionDetails) {
            map.put("@type", "Cockroachdb");
            if (((CockroachdbConnectionDetails) connectionDetails).getConnectionParams() != null) {
                map.put("connectionParams", ((CockroachdbConnectionDetails) connectionDetails).getConnectionParams());
            }
        } else if (connectionDetails instanceof RedshiftConnectionDetails) {
            map.put("@type", "Redshift");
        } else if (connectionDetails instanceof AthenaConnectionDetails) {
            map.put("@type", "Athena");
            map.put("s3OutputLocation", ((AthenaConnectionDetails) connectionDetails).getS3OutputLocation());
            map.put("workgroup", ((AthenaConnectionDetails) connectionDetails).getWorkgroup());
        } else if (connectionDetails instanceof PostgresConnectionDetails) {
            map.put("@type", "Postgres");
            if (((PostgresConnectionDetails) connectionDetails).getConnectionParams() != null) {
                map.put("connectionParams", ((PostgresConnectionDetails) connectionDetails).getConnectionParams());
            }
        } else if (connectionDetails instanceof SparkConnectionDetails) {
            map.put("@type", "Spark");
        } else if (connectionDetails instanceof MongoDBConnectionDetails) {
            map.put("@type", "MongoDB");
        } else if (connectionDetails instanceof KafkaConnectionDetails) {
            map.put("@type", "Kafka");
            map.put("isSecure", BooleanUtils.toString(((KafkaConnectionDetails) connectionDetails).getIsSecure(), "true", "false", "false"));
        } else if (connectionDetails instanceof SnowflakeConnectionDetails) {
            map.put("@type", "Snowflake");
            map.put("account", ((SnowflakeConnectionDetails) connectionDetails).getAccount());
            if (((SnowflakeConnectionDetails) connectionDetails).getAdditionalParameters() != null) {
                map.put("additionalParameters", ((SnowflakeConnectionDetails) connectionDetails).getAdditionalParameters());
            }
            map.put("schemaName", ((SnowflakeConnectionDetails) connectionDetails).getSchemaName());
        } else if (connectionDetails instanceof BigqueryConnectionDetails) {
            map.put("@type", "BigQuery");
            map.put("email", ((BigqueryConnectionDetails) connectionDetails).getEmail());
            map.put("dataset", ((BigqueryConnectionDetails) connectionDetails).getDatabaseName());
            map.put("projectId", ((BigqueryConnectionDetails) connectionDetails).getProjectId());
            map.put("privateKey", ((BigqueryConnectionDetails) connectionDetails).getPrivateKey());
        } else {
            throw new RuntimeException("Cannot find a mapper for " + connectionDetails.getClass().getSimpleName());
        }
        if (connectionDetails.getDatabaseName() != null) {
            map.put("databaseName", connectionDetails.getDatabaseName());
        }
        if (connectionDetails.getServerIp() != null) {
            map.put("serverIp", connectionDetails.getServerIp());
        }
        if (connectionDetails.getServerPort() != null) {
            map.put("serverPort", String.valueOf(connectionDetails.getServerPort()));
        }
        return map;
    }
}
