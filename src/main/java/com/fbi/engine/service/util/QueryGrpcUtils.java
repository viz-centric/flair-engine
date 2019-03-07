package com.fbi.engine.service.util;

import com.fbi.engine.service.constant.GrpcConstants;
import com.flair.bi.messages.Query;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.project.bi.query.dto.ConditionExpressionDTO;
import com.project.bi.query.dto.QueryDTO;
import com.project.bi.query.dto.SortDTO;
import com.project.bi.query.expression.condition.ConditionExpression;
import com.project.bi.query.expression.condition.impl.AndConditionExpression;
import com.project.bi.query.expression.condition.impl.OrConditionExpression;

import java.util.List;
import java.util.stream.Collectors;

public final class QueryGrpcUtils {

    public static QueryDTO mapToQueryDTO(Query request){
        QueryDTO queryDTO = new QueryDTO();
        queryDTO.setSource(request.getSource());
        queryDTO.setFields(request.getFieldsList());
        queryDTO.setGroupBy(request.getGroupByList());
        queryDTO.setLimit(request.getLimit());
        queryDTO.setEnableCaching(request.getEnableCaching());
        queryDTO.setDistinct(request.getDistinct());
        queryDTO.setOrders(getListSortDTO(request.getOrdersList()));
        queryDTO.setConditionExpressions(getListConditionExpressionDTO(request.getConditionExpressionsList()));
        return queryDTO;
    }

    private static List<SortDTO> getListSortDTO(List<Query.SortHolder> orders){
        return orders.stream().map(order -> {
            SortDTO sortDTO = new SortDTO();
            sortDTO.setFeatureName(order.getFeatureName());
            sortDTO.setDirection(order.getDirectionValue() == 0 ? SortDTO.Direction.ASC: SortDTO.Direction.DESC);
            return sortDTO;
        }).collect(Collectors.toList());
    }

    private static List<ConditionExpressionDTO> getListConditionExpressionDTO(List<Query.ConditionExpressionHolder> conditionExpressions){
        return conditionExpressions.stream().map(conditionExpressionHolder -> {
            ConditionExpressionDTO conditionExpressionDTO = new ConditionExpressionDTO();
            conditionExpressionDTO.setSourceType(getFilterSourceType(conditionExpressionHolder.getSourceTypeValue()));
            conditionExpressionDTO.setConditionExpression((conditionExpressionHolder.getExpressionTypeValue() > 1) ?
                createConditionExpression(conditionExpressionHolder.getConditionExpression(), conditionExpressionHolder.getExpressionTypeValue()):
                createAndOrConditionExpression(conditionExpressionHolder.getConditionExpression(), conditionExpressionHolder.getExpressionTypeValue(), conditionExpressionHolder.getAndOrExpressionType()));
            return conditionExpressionDTO;
        }).collect(Collectors.toList());
    }

    private static ConditionExpressionDTO.SourceType getFilterSourceType(Integer value) {
        if(value == 0) {
            return ConditionExpressionDTO.SourceType.BASE;
        } else if(value == 1) {
            return ConditionExpressionDTO.SourceType.FILTER;
        }else if(value == 2) {
            return ConditionExpressionDTO.SourceType.REDUCTION;
        }
        return null;
    }

    private static ConditionExpression createConditionExpression(String conditionExpressionString, Integer expressionType) {
        return new Gson().fromJson(
            conditionExpressionString,
            getConditionExpressionInstance(expressionType).getClass()
        );
    }

    private static ConditionExpression createAndOrConditionExpression(String conditionExpressionString, Integer expressionType, Query.ConditionExpressionHolder.AndOrExpressionType andOrExpressionType) {
        JsonParser parser = new JsonParser();
        JsonElement jsonTree = parser.parse(conditionExpressionString);
        if(jsonTree.isJsonObject()) {
            ConditionExpression firstExpression = new Gson().fromJson(
                jsonTree.getAsJsonObject().get(GrpcConstants.FIRST_EXPRESSION).toString(),
                getConditionExpressionInstance(andOrExpressionType.getFirstExpressionTypeValue()).getClass()
            );

            ConditionExpression secondExpression = new Gson().fromJson(
                jsonTree.getAsJsonObject().get(GrpcConstants.SECOND_EXPRESSION).toString(),
                getConditionExpressionInstance(andOrExpressionType.getSecondExpressionTypeValue()).getClass()
            );

            return (expressionType == 0) ?
                createAndConditionExpression(firstExpression, secondExpression):
                createOrConditionExpression(firstExpression, secondExpression);
        }
        return null;
    }

    private static ConditionExpression createAndConditionExpression(ConditionExpression firstConditionExpression, ConditionExpression secondConditionExpression) {
        AndConditionExpression andConditionExpression = new AndConditionExpression();
        andConditionExpression.setFirstExpression(firstConditionExpression);
        andConditionExpression.setSecondExpression(secondConditionExpression);
        return andConditionExpression;
    }

    private static ConditionExpression createOrConditionExpression(ConditionExpression firstConditionExpression, ConditionExpression secondConditionExpression) {
        OrConditionExpression orConditionExpression = new OrConditionExpression();
        orConditionExpression.setFirstExpression(firstConditionExpression);
        orConditionExpression.setSecondExpression(secondConditionExpression);
        return orConditionExpression;
    }

    private static ConditionExpression getConditionExpressionInstance(Integer type) {
        switch(type) {
            case 0:
                return GrpcConstants.andConditionExpression;
            case 1:
                return GrpcConstants.orConditionExpression;
            case 2:
                return GrpcConstants.betweenConditionExpression;
            case 3:
                return GrpcConstants.compareConditionExpression;
            case 4:
                return GrpcConstants.containsConditionExpression;
            case 5:
                return GrpcConstants.notContainsConditionExpression;
            case 6:
                return GrpcConstants.likeConditionExpression;
            default:
                return null;
        }
    }
}
