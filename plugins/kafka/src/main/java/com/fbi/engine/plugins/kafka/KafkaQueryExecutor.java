package com.fbi.engine.plugins.kafka;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fbi.engine.api.DataSourceConnection;
import com.fbi.engine.api.KafkaQuery;
import com.fbi.engine.api.Query;
import com.fbi.engine.api.QueryExecutor;
import com.fbi.engine.plugins.kafka.dto.KafkaKsqlDescribeResponse;
import com.fbi.engine.plugins.kafka.dto.KafkaQueryResponse;
import com.fbi.engine.plugins.kafka.dto.KafkaShowTablesResponse;
import com.flair.bi.compiler.AbstractFlairCompiler;
import com.flair.bi.compiler.SQLListener;
import com.flair.bi.compiler.kafka.KafkaListener;
import com.google.common.collect.ImmutableMap;
import com.project.bi.exceptions.CompilationException;
import com.project.bi.exceptions.ExecutionException;
import com.project.bi.query.FlairCompiler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class KafkaQueryExecutor implements QueryExecutor {

	public static final String DESCRIBE_STMT = "DESCRIBE ";
	public static final String ENDPOINT_KSQL = "/ksql";

	private final RestTemplate template;
	private final FlairCompiler compiler;
	private final ObjectMapper objectMapper;
	private final DataSourceConnection connection;

	@Override
	public void execute(Query query, Writer writer) throws ExecutionException {
		KafkaQuery kafkaQuery = (KafkaQuery) query;
		executeKafkaQuery(writer, kafkaQuery);
	}

	private void executeKafkaQuery(Writer writer, KafkaQuery kafkaQuery) throws ExecutionException {
		ObjectNode objectNode = objectMapper.createObjectNode();

		Map<String, String> metadata = null;
		if (kafkaQuery.isMetadataRetrieved()) {
			metadata = getQueryMetadata(kafkaQuery);
		}

		List<Map<String, Object>> data;
		if (Objects.equals(kafkaQuery.getQuery(), KafkaListener.QUERY__SHOW_TABLES_AND_STREAMS)) {
			data = getShowTablesResult();
		} else {
			List<String> columns = null;
            if (metadata != null) {
                columns = new ArrayList<>(metadata.keySet());
            }
            data = getQueryResult(kafkaQuery, kafkaQuery.getQuery(), columns);
		}

		if (metadata != null) {
			objectNode.putPOJO("metadata", metadata);
		}

		objectNode.putPOJO("data", data);

		try {
			objectMapper.writeValue(writer, objectNode);
		} catch (IOException e) {
			throw new ExecutionException(e);
		}
	}

	private List<Map<String, Object>> getQueryResult(KafkaQuery query, String statement, List<String> columns) throws ExecutionException {
		List<String> selectColumns;
        if (columns != null) {
            selectColumns = columns;
        } else {
            selectColumns = getQueryColumns(query);
        }
		if (selectColumns == null || selectColumns.isEmpty()) {
			return new ArrayList<>();
		}

		KafkaQueryResponse[] results = makeKafkaQueryRequest(statement);

		return Arrays.stream(results).filter(item -> {
			List<Object> resultColumns = item.getRow().getColumns();
			if (selectColumns.size() != resultColumns.size()) {
				throw new RuntimeException(new ExecutionException("Select columns count " + selectColumns.size()
						+ " is not the same as the query result count " + resultColumns.size()));
			}
			return true;
		}).map(item -> {
			List<Object> resultColumns = item.getRow().getColumns();
			Map<String, Object> map = new HashMap<>();
			for (int i = 0; i < resultColumns.size(); i++) {
				Object val = resultColumns.get(i);
				String key = selectColumns.get(i);
				map.put(key, val);
			}
			return map;
		}).collect(Collectors.toList());
	}

	private List<Map<String, Object>> getShowTablesResult() throws ExecutionException {
		KafkaShowTablesResponse[] tableResults = makeKafkaRequest(ENDPOINT_KSQL, "show tables",
				KafkaShowTablesResponse[].class);
		KafkaShowTablesResponse[] streamResults = makeKafkaRequest(ENDPOINT_KSQL, "show streams",
				KafkaShowTablesResponse[].class);

		List<KafkaShowTablesResponse.Table> results = new ArrayList<>();

		if (tableResults.length == 1) {
			results.addAll(tableResults[0].getTables());
		}

		if (streamResults.length == 1) {
			results.addAll(streamResults[0].getStreams());
		}

		return results.stream().map(table -> table.getName()).collect(Collectors.toSet()).stream()
				.map(tableName -> ImmutableMap.of("tablename", (Object) tableName)).collect(Collectors.toList());
	}

	private List<String> getQueryColumns(KafkaQuery query) {

		final StringWriter writer = new StringWriter();
		try {
			compiler.compile(query.getFlairQuery(), writer);
		} catch (CompilationException e) {
			log.error("Error compiling the query " + query.getFlairQuery().getStatement(), e);
			return new ArrayList<>();
		}

		AbstractFlairCompiler acompiler = (AbstractFlairCompiler) compiler;
		SQLListener listener = (SQLListener) acompiler.getListener();

		return listener.getParseResults(SQLListener.ParseResult.SELECT_COLUMNS);
	}

	private Map<String, String> getQueryMetadata(KafkaQuery kafkaQuery) throws ExecutionException {
		String statement = DESCRIBE_STMT + kafkaQuery.getSource();
		KafkaKsqlDescribeResponse[] result = makeKafkaRequest(ENDPOINT_KSQL, statement,
				KafkaKsqlDescribeResponse[].class);
		return Arrays.asList(result).get(0).getSourceDescription().getFields().stream()
				.collect(Collectors.toMap(f -> f.getName(), f -> f.getSchema().getType()));
	}

	private <T> T makeKafkaRequest(String endpoint, String statement, Class<T> responseClass)
			throws ExecutionException {
		StringBuilder url = new StringBuilder(connection.getConnectionString()).append(endpoint);

		Map<String, Object> params = createRequestParams(statement);
		LinkedMultiValueMap<String, String> headers = createRequestHeaders();

		try {

			ResponseEntity<T> result = template.exchange(url.toString(), HttpMethod.POST,
					new HttpEntity<>(params, headers), responseClass);

			log.info("Kafka response received {}", result.getBody());

			return result.getBody();

		} catch (HttpClientErrorException e) {
			log.error("Error making a KSQL request " + e.getResponseBodyAsString() + " for statement " + statement, e);
			throw new ExecutionException(e);
		} catch (Exception e) {
			throw new ExecutionException(e);
		}
	}

	private LinkedMultiValueMap<String, String> createRequestHeaders() {
		LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Accept", "application/vnd.ksql.v1+json");
		headers.add("Content-Type", "application/vnd.ksql.v1+json");
		return headers;
	}

	private KafkaQueryResponse[] makeKafkaQueryRequest(String statement) throws ExecutionException {
		StringBuilder url = new StringBuilder(connection.getConnectionString()).append("/query");

		Map<String, Object> params = createRequestParams(statement);

		LinkedMultiValueMap<String, String> headers = createRequestHeaders();

		try {

			ResponseEntity<String> result = template.exchange(url.toString(), HttpMethod.POST,
					new HttpEntity<>(params, headers), String.class);

			List<KafkaQueryResponse> array = new ArrayList<>();
			JsonFactory jsonFactory = new JsonFactory();
			try (BufferedReader br = new BufferedReader(new StringReader(result.getBody()))) {
				Iterator<KafkaQueryResponse> value = objectMapper.readValues(jsonFactory.createParser(br),
						KafkaQueryResponse.class);
				value.forEachRemaining(e -> {
					if (e.getRow() != null) {
						array.add(e);
					}
				});
			}

			log.info("Kafka chunked response received {}", result.getBody());

			return array.toArray(new KafkaQueryResponse[] {});

		} catch (HttpClientErrorException e) {
			log.error("Error making a KSQL request " + e.getResponseBodyAsString() + " for statement " + statement, e);
			throw new ExecutionException(e);
		} catch (Exception e) {
			throw new ExecutionException(e);
		}
	}

	private Map<String, Object> createRequestParams(String statement) {
		Map<String, Object> params = new HashMap<>();
		params.put("ksql", statement + ";");
		params.put("streamsProperties", ImmutableMap.of("ksql.streams.auto.offset.reset", "earliest"));
		return params;
	}

}