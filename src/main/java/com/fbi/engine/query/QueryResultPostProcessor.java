package com.fbi.engine.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.config.jackson.ResultSetSerializer;
import com.project.bi.query.FlairQuery;
import com.project.bi.query.dto.FieldDTO;
import com.project.bi.query.dto.QueryDTO;
import com.project.bi.query.dto.TransformationDTO;
import com.project.bi.query.dto.transform.GroupType;
import com.project.bi.query.dto.transform.GroupingTransformationDTO;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class QueryResultPostProcessor {

    private final ObjectMapper objectMapper;
    private static final WeekFields WEEK_FIELDS = WeekFields.of(DayOfWeek.MONDAY, 7);

    @SneakyThrows
    public String process(FlairQuery flairQuery, String result) {
        QueryDTO queryDTO = flairQuery.getQueryDTO();
        if (queryDTO == null) {
            return result;
        }
        Map<String, Object> resultMap = objectMapper.readValue(result, Map.class);
        List<TransformationDTO> transformations = queryDTO.getTransformations();
        for (TransformationDTO transformationDTO : transformations) {
            if (transformationDTO instanceof GroupingTransformationDTO) {
                GroupingTransformationDTO groupingTransformationDTO = (GroupingTransformationDTO) transformationDTO;
                resultMap = processGroupTransformation(queryDTO, groupingTransformationDTO, resultMap);
            } else {
                log.warn("Ignoring transformation {} for query {}",
                        transformationDTO.getClass().getSimpleName(), flairQuery.getStatement());
            }
        }
        return objectMapper.writeValueAsString(resultMap);
    }

    private Map<String, Object> processGroupTransformation(QueryDTO queryDTO, GroupingTransformationDTO groupingTransformationDTO, Map<String, Object> result) {
        FieldDTO groupingField = groupingTransformationDTO.getGroupingField();

        List<FieldDTO> fields = queryDTO.getFields();
        List<Map<String, Object>> data = (List<Map<String, Object>>) result.get("data");

        Optional<Map<String, Object>> firstRow = data.stream()
                .findFirst();

        if (!firstRow.isPresent()) {
            return result;
        }

        Map<String, FieldDTO> aggregationFields = fields.stream()
                .filter(f -> f.getAggregation() != null)
                .collect(Collectors.toMap(f -> f.getName(), f -> f));

        Map<String, FieldDTO> nonAggregationFields = fields.stream()
                .filter(f -> f.getAggregation() == null)
                .collect(Collectors.toMap(f -> f.getName(), f -> f));

        Map<List<GroupKey>, List<Map<String, Object>>> grouped = data
                .stream()
                .collect(Collectors.groupingBy(dataRow ->
                        dataRow
                        .keySet()
                        .stream()
                        .filter(k -> nonAggregationFields.containsKey(k))
                        .map(k -> {
                            if (Objects.equals(groupingField.getName(), k)) {
                                Object value = defineGroupingKey(groupingTransformationDTO, groupingField, dataRow);
                                return new GroupKey(k, value, true);
                            }
                            return new GroupKey(k, dataRow.get(k), false);
                        })
                        .collect(Collectors.toList())));


        List<Map<String, Object>> newResult = grouped.keySet()
                .stream()
                .map((List<GroupKey> key) -> {
                    List<Map<String, Object>> groupedRows = grouped.get(key);

                    Map<String, Object> jointMap = new HashMap<>();
                    key.forEach(k -> jointMap.put(k.getColumn(), k.getValue()));

                    aggregationFields.values()
                            .forEach(af ->
                                    groupedRows
                                    .forEach(groupedRow -> {
                                        Number groupedRowResult = (Number) groupedRow.get(af.getName());
                                        Number aggregatedValue = (Number) jointMap.get(af.getName());
                                        if (aggregatedValue == null) {
                                            jointMap.put(af.getName(), groupedRowResult);
                                        } else {
                                            jointMap.put(af.getName(), aggregateNumbers(af, aggregatedValue, groupedRowResult));
                                        }
                                    }));

                    return jointMap;
                })
                .collect(Collectors.toList());

        result.put("data", newResult);

        return result;
    }

    private Object defineGroupingKey(GroupingTransformationDTO groupingTransformationDTO, FieldDTO groupingField, Map<String, Object> dataRow) {
        GroupType groupType = groupingTransformationDTO.getGroupType();

        String dateStr = (String) dataRow.get(groupingField.getName());
        LocalDate date = LocalDate.parse(dateStr, ResultSetSerializer.DATE_TIME_FORMATTER);
        switch (groupType) {
            case WEEK:
                return date.getYear() + "-" + twoDigits(date.get(WEEK_FIELDS.weekOfYear()));
            case DAY:
                return date.getYear() + "-" + twoDigits(date.getMonthValue()) + "-" + twoDigits(date.getDayOfMonth());
            case MONTH:
                return date.getYear() + "-" + twoDigits(date.getMonthValue());
            case QUARTER:
                if (date.getMonthValue() <= 3) {
                    return date.getYear() + "-" + 1;
                }
                if (date.getMonthValue() <= 6) {
                    return date.getYear() + "-" + 2;
                }
                if (date.getMonthValue() <= 9) {
                    return date.getYear() + "-" + 3;
                }
                return date.getYear() + "-" + 4;
            case YEAR:
                return date.getYear();
        }
        return null;
    }

    private String twoDigits(Object value) {
        return value.toString().length() > 1 ? value.toString() : "0" + value.toString();
    }

    private Number aggregateNumbers(FieldDTO field, Number val1, Number val2) {
        String aggregation = field.getAggregation();
        if ("SUM".equalsIgnoreCase(aggregation) || "COUNT".equalsIgnoreCase(aggregation)) {
            if (val1 instanceof Double) {
                return val1.doubleValue() + val2.doubleValue();
            }
            if (val1 instanceof Float) {
                return val1.floatValue() + val2.floatValue();
            }
            return val1.longValue() + val2.longValue();
        } else if ("AVG".equalsIgnoreCase(aggregation)) {
            if (val1 instanceof Double) {
                return (val1.doubleValue() + val2.doubleValue()) / 2;
            }
            if (val1 instanceof Float) {
                return (val1.floatValue() + val2.floatValue()) / 2;
            }
            return (val1.longValue() + val2.longValue()) / 2;
        } else if ("MIN".equalsIgnoreCase(aggregation)) {
            if (val1 instanceof Double) {
                return Math.min(val1.doubleValue(), val2.doubleValue());
            }
            if (val1 instanceof Float) {
                return Math.min(val1.floatValue(), val2.floatValue());
            }
            return Math.min(val1.longValue(), val2.longValue());
        } else if ("MAX".equalsIgnoreCase(aggregation)) {
            if (val1 instanceof Double) {
                return Math.max(val1.doubleValue(), val2.doubleValue());
            }
            if (val1 instanceof Float) {
                return Math.max(val1.floatValue(), val2.floatValue());
            }
            return Math.max(val1.longValue(), val2.longValue());
        }
        return val1;
    }

    @Data
    @RequiredArgsConstructor
    private static class GroupKey {
        final String column;
        final Object value;
        final boolean isGroupKey;
    }

}
