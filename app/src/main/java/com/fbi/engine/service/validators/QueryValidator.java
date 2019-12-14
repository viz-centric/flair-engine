package com.fbi.engine.service.validators;

import com.fbi.engine.service.constant.GrpcErrors;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;
import com.project.bi.query.dto.ConditionExpressionDTO;
import com.project.bi.query.dto.QueryDTO;
import com.project.bi.query.expression.condition.CompositeConditionExpression;
import com.project.bi.query.expression.condition.SimpleConditionExpression;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class QueryValidator {

    public QueryValidationResult validate(QueryDTO query) {
        List<String> featureNames = query.getConditionExpressions()
            .stream()
            .map(ConditionExpressionDTO::getConditionExpression)
            .filter(c -> c instanceof CompositeConditionExpression)
            .map(c -> (CompositeConditionExpression) c)
            .filter(c -> c.getFirstExpression() instanceof SimpleConditionExpression && c.getSecondExpression() instanceof SimpleConditionExpression)
            .flatMap(c -> Arrays.stream(new String[] {((SimpleConditionExpression) c.getFirstExpression()).getFeatureName(), ((SimpleConditionExpression) c.getSecondExpression()).getFeatureName()}))
            .collect(Collectors.toList());

        Set<String> duplicatedFeatureNames =
            ImmutableMultiset.copyOf(featureNames)
                .entrySet()
                .stream()
                .filter(entry -> entry.getCount() > 1)
                .map(Multiset.Entry::getElement)
                .collect(Collectors.toSet());

        return new QueryValidationResult()
            .setErrors(duplicatedFeatureNames.isEmpty() ? null : GrpcErrors.DUPLICATE_FEATURES)
            .setFeatureNames(duplicatedFeatureNames);
    }

}
