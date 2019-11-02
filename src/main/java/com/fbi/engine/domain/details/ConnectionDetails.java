package com.fbi.engine.domain.details;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes(
    {@JsonSubTypes.Type(value = OracleConnectionDetails.class, name = "Oracle"),
        @JsonSubTypes.Type(value = MySqlConnectionDetails.class, name = "MySql"),
        @JsonSubTypes.Type(value = PostgresConnectionDetails.class, name = "Postgres"),
        @JsonSubTypes.Type(value = RedshiftConnectionDetails.class, name = "Redshift"),
        @JsonSubTypes.Type(value = AthenaConnectionDetails.class, name = "Athena"),
        @JsonSubTypes.Type(value = SparkConnectionDetails.class, name = "Spark"),
        @JsonSubTypes.Type(value = MongoDBConnectionDetails.class, name = "MongoDB"),
        @JsonSubTypes.Type(value = CockroachdbConnectionDetails.class, name = "Cockroachdb"),
        @JsonSubTypes.Type(value = KafkaConnectionDetails.class, name = "Kafka"),
        @JsonSubTypes.Type(value = SnowflakeConnectionDetails.class, name = "Snowflake")
    })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public abstract class ConnectionDetails implements Serializable {

    private String serverIp;

    private Integer serverPort;

    private String databaseName;

    @JsonIgnore
    public abstract String getConnectionString();
}
