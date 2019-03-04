package com.fbi.engine.service.constant;

import com.project.bi.query.expression.condition.ConditionExpression;
import com.project.bi.query.expression.condition.impl.AndConditionExpression;
import com.project.bi.query.expression.condition.impl.BetweenConditionExpression;
import com.project.bi.query.expression.condition.impl.CompareConditionExpression;
import com.project.bi.query.expression.condition.impl.ContainsConditionExpression;
import com.project.bi.query.expression.condition.impl.LikeConditionExpression;
import com.project.bi.query.expression.condition.impl.NotContainsConditionExpression;
import com.project.bi.query.expression.condition.impl.OrConditionExpression;

public class GrpcConstants {

    public final static String FIRST_EXPRESSION = "firstExpression";
    public final static String SECOND_EXPRESSION = "secondExpression";

    public final static ConditionExpression andConditionExpression = new AndConditionExpression();
    public final static ConditionExpression orConditionExpression = new OrConditionExpression();
    public final static ConditionExpression betweenConditionExpression = new BetweenConditionExpression();
    public final static ConditionExpression compareConditionExpression = new CompareConditionExpression();
    public final static ConditionExpression containsConditionExpression = new ContainsConditionExpression();
    public final static ConditionExpression notContainsConditionExpression = new NotContainsConditionExpression();
    public final static ConditionExpression likeConditionExpression = new LikeConditionExpression();

    public final static String CONNECTION_EXISTS = "Connection already exists";
    public final static String CONNECTION_NOT_FOUND = "Connection does not exist";
    public final static String ABORTED_INTERNAL = "Stream aborted due to internal error";
}
