package com.fbi.engine.service.validators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.project.bi.query.dto.ConditionExpressionDTO;
import com.project.bi.query.dto.QueryDTO;
import com.project.bi.query.expression.condition.impl.AndConditionExpression;
import com.project.bi.query.expression.condition.impl.BetweenConditionExpression;
import com.project.bi.query.expression.condition.impl.OrConditionExpression;

public class QueryValidatorTest {

	private QueryValidator queryValidator;

	@BeforeEach
	public void setUp() throws Exception {
		queryValidator = new QueryValidator();
	}

	@Test
	public void validateReturnsSuccessIfNonCompositeConditionExpression() {
		ConditionExpressionDTO expr1 = new ConditionExpressionDTO();
		expr1.setConditionExpression(new BetweenConditionExpression());

		QueryDTO query = new QueryDTO();
		query.setConditionExpressions(Arrays.asList(expr1));

		QueryValidationResult validate = queryValidator.validate(query);
		assertTrue(validate.isSuccess());
	}

	@Test
	public void validateReturnsSuccessIfNonSimpleConditionExpression() {
		ConditionExpressionDTO expr1 = new ConditionExpressionDTO();
		AndConditionExpression conditionExpression = new AndConditionExpression();
		conditionExpression.setFirstExpression(new BetweenConditionExpression());
		conditionExpression.setSecondExpression(new OrConditionExpression());
		expr1.setConditionExpression(conditionExpression);

		QueryDTO query = new QueryDTO();
		query.setConditionExpressions(Arrays.asList(expr1));

		QueryValidationResult validate = queryValidator.validate(query);
		assertTrue(validate.isSuccess());
	}

	@Test
	public void validateReturnsSuccessIfDifferentFeatureNames() {
		ConditionExpressionDTO expr1 = new ConditionExpressionDTO();
		AndConditionExpression conditionExpression = new AndConditionExpression();
		BetweenConditionExpression firstExpression = new BetweenConditionExpression();
		firstExpression.setFeatureName("State");
		conditionExpression.setFirstExpression(firstExpression);
		BetweenConditionExpression secondExpression = new BetweenConditionExpression();
		secondExpression.setFeatureName("City");
		conditionExpression.setSecondExpression(secondExpression);
		expr1.setConditionExpression(conditionExpression);

		QueryDTO query = new QueryDTO();
		query.setConditionExpressions(Arrays.asList(expr1));

		QueryValidationResult validate = queryValidator.validate(query);
		assertTrue(validate.isSuccess());
	}

	@Test
	public void validateReturnsDuplicateErrorIfSameFeatureNames() {
		ConditionExpressionDTO expr1 = new ConditionExpressionDTO();
		AndConditionExpression conditionExpression = new AndConditionExpression();
		BetweenConditionExpression firstExpression = new BetweenConditionExpression();
		firstExpression.setFeatureName("State");
		conditionExpression.setFirstExpression(firstExpression);
		BetweenConditionExpression secondExpression = new BetweenConditionExpression();
		secondExpression.setFeatureName("State");
		conditionExpression.setSecondExpression(secondExpression);
		expr1.setConditionExpression(conditionExpression);

		QueryDTO query = new QueryDTO();
		query.setConditionExpressions(Arrays.asList(expr1));

		QueryValidationResult validate = queryValidator.validate(query);
		assertTrue(validate.isError());
		assertEquals(1, validate.getFeatureNames().size());
		assertTrue(validate.getFeatureNames().contains("State"));
	}

	@Test
	public void validateReturnsDuplicateErrorIfSameFeatureNamesInDifferentExpressions() {
		ConditionExpressionDTO expr1 = new ConditionExpressionDTO();
		AndConditionExpression conditionExpression = new AndConditionExpression();
		BetweenConditionExpression firstExpression = new BetweenConditionExpression();
		firstExpression.setFeatureName("State");
		conditionExpression.setFirstExpression(firstExpression);
		BetweenConditionExpression secondExpression = new BetweenConditionExpression();
		secondExpression.setFeatureName("City");
		conditionExpression.setSecondExpression(secondExpression);
		expr1.setConditionExpression(conditionExpression);

		ConditionExpressionDTO expr2 = new ConditionExpressionDTO();
		conditionExpression = new AndConditionExpression();
		firstExpression = new BetweenConditionExpression();
		firstExpression.setFeatureName("City");
		conditionExpression.setFirstExpression(firstExpression);
		secondExpression = new BetweenConditionExpression();
		secondExpression.setFeatureName("State");
		conditionExpression.setSecondExpression(secondExpression);
		expr2.setConditionExpression(conditionExpression);

		QueryDTO query = new QueryDTO();
		query.setConditionExpressions(Arrays.asList(expr1, expr2));

		QueryValidationResult validate = queryValidator.validate(query);
		assertTrue(validate.isError());
		assertEquals(2, validate.getFeatureNames().size());
		assertTrue(validate.getFeatureNames().contains("State"));
		assertTrue(validate.getFeatureNames().contains("City"));
	}
}
