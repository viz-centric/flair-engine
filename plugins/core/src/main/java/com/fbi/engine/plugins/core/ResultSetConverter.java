package com.fbi.engine.plugins.core;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.project.bi.general.Converter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ResultSetConverter implements Converter<ResultSet, String> {

	private final ObjectMapper objectMapper;

	private final boolean withMeta;

	/**
	 * Convert given input to output
	 *
	 * @param data input data
	 * @return output
	 */
	@Override
	public String convert(ResultSet data) {
		try {
			StringWriter stringWriter = new StringWriter();

			ObjectNode objectNode = objectMapper.createObjectNode();
			objectNode.putPOJO("data", data);

			if (withMeta) {
				int columncount = data.getMetaData().getColumnCount();
				Map<String, String> map = new HashMap<>();
				for (int x = 1; x <= columncount; x++) {
					map.put(data.getMetaData().getColumnName(x), data.getMetaData().getColumnTypeName(x));
				}
				objectNode.putPOJO("metadata", map);
			}

			writeValue(stringWriter, objectNode);

			return stringWriter.toString();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void writeValue(StringWriter stringWriter, ObjectNode objectNode) {
		try {
			objectMapper.writeValue(stringWriter, objectNode);
		} catch (IOException e) {
			log.error("Error writing json value", e);
		}
	}
}
