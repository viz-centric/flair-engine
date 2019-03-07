package com.fbi.engine.web.rest;

import com.fbi.engine.FbiengineApp;
import com.fbi.engine.domain.Connection;
import com.fbi.engine.query.QueryExecutor;
import com.fbi.engine.query.abstractfactory.QueryAbstractFactory;
import com.fbi.engine.query.factory.FlairFactory;
import com.fbi.engine.repository.ConnectionRepository;
import com.project.bi.exceptions.CompilationException;
import com.project.bi.query.FlairCompiler;
import com.project.bi.query.FlairQuery;
import com.project.bi.query.dto.ConditionExpressionDTO;
import com.project.bi.query.dto.QueryDTO;
import com.project.bi.query.expression.condition.impl.AndConditionExpression;
import com.project.bi.query.expression.condition.impl.BetweenConditionExpression;
import com.project.bi.query.expression.condition.impl.CompareConditionExpression;
import com.project.bi.query.expression.condition.impl.CompareConditionExpression.ComparatorType;
import com.project.bi.query.expression.condition.impl.ContainsConditionExpression;
import com.project.bi.query.expression.condition.impl.LikeConditionExpression;
import com.project.bi.query.expression.condition.impl.OrConditionExpression;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FbiengineApp.class)
public class MongoDBQueryTest {

    private Logger log = LoggerFactory.getLogger(getClass());

    private static final String MONGODB_ID = "c2bf63b2-2b0d-4532-a246-7e414c5a58e1";

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private QueryAbstractFactory queryAbstractFactory;

    private Connection connection;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        connection = connectionRepository.findByLinkId(MONGODB_ID);
    }


    @Test
    @Transactional
    public void equalOperatorTest() throws Exception {


        List<String> operators = Arrays.asList("=", ">", "<", "<=", ">=", "<>");

        String expectedQuery = "";

        // Create Query
        QueryDTO queryDto = new QueryDTO();

        queryDto.setFields(Arrays.asList("*"));
        ConditionExpressionDTO expDto = new ConditionExpressionDTO();

        CompareConditionExpression copmareCdt = new CompareConditionExpression();
        copmareCdt.setFeatureName("order_item_id");
        copmareCdt.setValue("1");

        for (String opr : operators) {


            switch (opr) {

                case "=": {
                    copmareCdt.setComparatorType(ComparatorType.EQ);
                    expectedQuery = "ecommerce.aggregate([{$match:{order_item_id:1}}])";
                }
                break;
                case ">": {
                    copmareCdt.setComparatorType(ComparatorType.GT);
                    expectedQuery = "ecommerce.aggregate([{$match:{order_item_id:{$gt:1}}}])";
                }
                break;
                case "<": {
                    copmareCdt.setComparatorType(ComparatorType.LT);
                    expectedQuery = "ecommerce.aggregate([{$match:{order_item_id:{$lt:1}}}])";
                }
                break;
                case "<=": {
                    copmareCdt.setComparatorType(ComparatorType.LTE);
                    expectedQuery = "ecommerce.aggregate([{$match:{order_item_id:{$lte:1}}}])";
                }
                break;
                case ">=": {
                    copmareCdt.setComparatorType(ComparatorType.GTE);
                    expectedQuery = "ecommerce.aggregate([{$match:{order_item_id:{$gte:1}}}])";
                }
                break;
                case "<>": {
                    copmareCdt.setComparatorType(ComparatorType.NEQ);
                    expectedQuery = "ecommerce.aggregate([{$match:{order_item_id:{$ne:1}}}])";
                }
                break;

            }

            expDto.setConditionExpression(copmareCdt);
            queryDto.setConditionExpressions(Arrays.asList(expDto));
            queryDto.setSource("ecommerce");


            FlairQuery query = new FlairQuery(queryDto.interpret(), queryDto.isMetaRetrieved());

            FlairFactory flairFactory = queryAbstractFactory.getQueryFactory(connection.getConnectionType().getBundleClass());

            FlairCompiler compiler = flairFactory.getCompiler();

            QueryExecutor executor = flairFactory.getExecutor(connection);

            StringWriter writer = new StringWriter();

            try {
                compiler.compile(query, writer);
            } catch (CompilationException e) {
                e.printStackTrace();
            }

            log.info("Expected Query : {}", expectedQuery);
            log.info("Genrated Query : {}", writer.toString());

            assertThat(expectedQuery).isEqualToIgnoringCase(writer.toString());

        }


        //   retVal = queryService.executeQuery(connection, query);


    }


    @Test
    @Transactional
    public void inOperatorTest() throws Exception {


        String expectedQuery = "ecommerce.aggregate([{$match:{PRODUCT_NAME:{$in:['Xioami']}}}])";
        // Create Query
        QueryDTO queryDto = new QueryDTO();


        queryDto.setFields(Arrays.asList("*"));
        ConditionExpressionDTO expDto = new ConditionExpressionDTO();

        ContainsConditionExpression containCdt = new ContainsConditionExpression();
        containCdt.setFeatureName("PRODUCT_NAME");

        containCdt.setValues(Arrays.asList("Xioami"));

        expDto.setConditionExpression(containCdt);

        queryDto.setConditionExpressions(Arrays.asList(expDto));

        queryDto.setSource("ecommerce");

        FlairQuery query = new FlairQuery(queryDto.interpret(), queryDto.isMetaRetrieved());


        FlairFactory flairFactory = queryAbstractFactory.getQueryFactory(connection.getConnectionType().getBundleClass());

        FlairCompiler compiler = flairFactory.getCompiler();

        QueryExecutor executor = flairFactory.getExecutor(connection);

        StringWriter writer = new StringWriter();

        try {
            compiler.compile(query, writer);
        } catch (CompilationException e) {
            e.printStackTrace();
        }

        log.info("Expected Query : {}", expectedQuery);
        log.info("Genrated Query : {}", writer.toString());

        assertThat(expectedQuery).isEqualToIgnoringCase(writer.toString());

//	       String retVal = queryService.executeQuery(connection, query);


    }

    @Test
    @Transactional
    public void orOperatorTest() throws Exception {


        List<String> operators = Arrays.asList("=", ">", "<", "<=", ">=", "<>");

        String expectedQuery = "";

        // Create Query
        QueryDTO queryDto = new QueryDTO();

        queryDto.setFields(Arrays.asList("*"));
        ConditionExpressionDTO expDto = new ConditionExpressionDTO();

        CompareConditionExpression copmareCdt1 = new CompareConditionExpression();
        copmareCdt1.setFeatureName("product_price");
        copmareCdt1.setValue("500");
        copmareCdt1.setComparatorType(ComparatorType.GTE);

        CompareConditionExpression copmareCdt2 = new CompareConditionExpression();
        copmareCdt2.setFeatureName("product_name");
        copmareCdt2.setValue("Team Golf New England Patriots Putter Grip");
        copmareCdt2.setComparatorType(ComparatorType.EQ);

        OrConditionExpression orCdt = new OrConditionExpression();
        orCdt.setFirstExpression(copmareCdt1);
        orCdt.setSecondExpression(copmareCdt2);

        expDto.setConditionExpression(orCdt);
        queryDto.setConditionExpressions(Arrays.asList(expDto));
        queryDto.setSource("ecommerce");

        expectedQuery = "ecommerce.aggregate([{$match:{$or:[{product_price:{$gte:500}},{product_name:'Team Golf New England Patriots Putter Grip'}]}}])";

        FlairQuery query = new FlairQuery(queryDto.interpret(), queryDto.isMetaRetrieved());

        FlairFactory flairFactory = queryAbstractFactory.getQueryFactory(connection.getConnectionType().getBundleClass());

        FlairCompiler compiler = flairFactory.getCompiler();

        QueryExecutor executor = flairFactory.getExecutor(connection);

        StringWriter writer = new StringWriter();

        try {
            compiler.compile(query, writer);
        } catch (CompilationException e) {
            e.printStackTrace();
        }

        log.info("Expected Query : {}", expectedQuery);
        log.info("Genrated Query : {}", writer.toString());

        assertThat(expectedQuery).isEqualToIgnoringCase(writer.toString());

    }


    @Test
    @Transactional
    public void andOperatorTest() throws Exception {


        List<String> operators = Arrays.asList("=", ">", "<", "<=", ">=", "<>");

        String expectedQuery = "";

        // Create Query
        QueryDTO queryDto = new QueryDTO();

        queryDto.setFields(Arrays.asList("*"));
        ConditionExpressionDTO expDto = new ConditionExpressionDTO();

        CompareConditionExpression copmareCdt1 = new CompareConditionExpression();
        copmareCdt1.setFeatureName("product_price");
        copmareCdt1.setValue("500");
        copmareCdt1.setComparatorType(ComparatorType.GTE);

        CompareConditionExpression copmareCdt2 = new CompareConditionExpression();
        copmareCdt2.setFeatureName("product_name");
        copmareCdt2.setValue("Team Golf New England Patriots Putter Grip");
        copmareCdt2.setComparatorType(ComparatorType.EQ);

        AndConditionExpression andCdt = new AndConditionExpression();
        andCdt.setFirstExpression(copmareCdt1);
        andCdt.setSecondExpression(copmareCdt2);

        expDto.setConditionExpression(andCdt);
        queryDto.setConditionExpressions(Arrays.asList(expDto));
        queryDto.setSource("ecommerce");

        expectedQuery = "ecommerce.aggregate([{$match:{$and:[{product_price:{$gte:500}},{product_name:'Team Golf New England Patriots Putter Grip'}]}}])";

        FlairQuery query = new FlairQuery(queryDto.interpret(), queryDto.isMetaRetrieved());

        FlairFactory flairFactory = queryAbstractFactory.getQueryFactory(connection.getConnectionType().getBundleClass());

        FlairCompiler compiler = flairFactory.getCompiler();

        QueryExecutor executor = flairFactory.getExecutor(connection);

        StringWriter writer = new StringWriter();

        try {
            compiler.compile(query, writer);
        } catch (CompilationException e) {
            e.printStackTrace();
        }

        log.info("Expected Query : {}", expectedQuery);
        log.info("Genrated Query : {}", writer.toString());

        assertThat(expectedQuery).isEqualToIgnoringCase(writer.toString());

    }

    @Test
    @Transactional
    public void betweenOperatorTest() throws Exception {


        String expectedQuery = "";

        // Create Query
        QueryDTO queryDto = new QueryDTO();

        queryDto.setFields(Arrays.asList("*"));
        ConditionExpressionDTO expDto = new ConditionExpressionDTO();

        BetweenConditionExpression copmareCdt1 = new BetweenConditionExpression();
        copmareCdt1.setFeatureName("product_price");
        copmareCdt1.setValue("500");
        copmareCdt1.setSecondValue("1000");

        expDto.setConditionExpression(copmareCdt1);
        queryDto.setConditionExpressions(Arrays.asList(expDto));
        queryDto.setSource("ecommerce");

        expectedQuery = "ecommerce.aggregate([{$match:{product_price:{$gte:500,$lte:1000}}}])";

        FlairQuery query = new FlairQuery(queryDto.interpret(), queryDto.isMetaRetrieved());

        FlairFactory flairFactory = queryAbstractFactory.getQueryFactory(connection.getConnectionType().getBundleClass());

        FlairCompiler compiler = flairFactory.getCompiler();

        QueryExecutor executor = flairFactory.getExecutor(connection);

        StringWriter writer = new StringWriter();

        try {
            compiler.compile(query, writer);
        } catch (CompilationException e) {
            e.printStackTrace();
        }

        log.info("Expected Query : {}", expectedQuery);
        log.info("Genrated Query : {}", writer.toString());

        assertThat(expectedQuery).isEqualToIgnoringCase(writer.toString());

    }

    @Test
    @Transactional
    public void likeOperatorTest() throws Exception {


        String expectedQuery = "";

        // Create Query
        QueryDTO queryDto = new QueryDTO();

        queryDto.setFields(Arrays.asList("*"));
        ConditionExpressionDTO expDto = new ConditionExpressionDTO();

        LikeConditionExpression likeCdt = new LikeConditionExpression();
        likeCdt.setFeatureName("product_name");
        likeCdt.setValue("o");

        expDto.setConditionExpression(likeCdt);
        queryDto.setConditionExpressions(Arrays.asList(expDto));
        queryDto.setSource("ecommerce");

        expectedQuery = "ecommerce.aggregate([{$match:{product_name:{$regex:'.*o.*'}}}])";

        FlairQuery query = new FlairQuery(queryDto.interpret(), queryDto.isMetaRetrieved());

        FlairFactory flairFactory = queryAbstractFactory.getQueryFactory(connection.getConnectionType().getBundleClass());

        FlairCompiler compiler = flairFactory.getCompiler();

        QueryExecutor executor = flairFactory.getExecutor(connection);

        StringWriter writer = new StringWriter();

        try {
            compiler.compile(query, writer);
        } catch (CompilationException e) {
            e.printStackTrace();
        }

        log.info("Expected Query : {}", expectedQuery);
        log.info("Genrated Query : {}", writer.toString());


        assertThat(expectedQuery).isEqualToIgnoringCase(writer.toString());

    }

    @Test
    @Transactional
    public void dateFunctionTest() throws Exception {


        String expectedQuery = "";

        // Create Query
        QueryDTO queryDto = new QueryDTO();

        queryDto.setFields(Arrays.asList("month(ORDER_DATE) as month, year(ORDER_DATE) as year, day(ORDER_DATE) as day", "hour(ORDER_DATE) as hr", "quarter(ORDER_DATE) as qt", "yearMonth(order_date) as ym", "yearWeek(ORDER_DATE) as yw", "yearQuarter(ORDER_DATE) as yq"));

        queryDto.setSource("ecommerce");

        expectedQuery = "ecommerce.aggregate([{$project:{month:{$month:'$ORDER_DATE'},year:{$year:'$ORDER_DATE'},day:{$dayOfMonth:'$ORDER_DATE'},hour:{$hour:'$ORDER_DATE'},quarter:{$substr: [{$add: [{$divide: [{$subtract: [{$month:'$ORDER_DATE'}, 1]}, 3]}, 1]}, 0, 1]},yearmonth:{$dateToString: { format: '%Y-%m', date:'$order_date'}},yearweek:{$dateToString: { format: '%Y-%V', date:'$ORDER_DATE'}},yearquarter:{$concat: [{$toString:{$year:'$ORDER_DATE'}},'-',{$substr: [{$add: [{$divide: [{$subtract: [{$month:'$ORDER_DATE'}, 1]}, 3]}, 1]}, 0, 1]}]}}}])";

        FlairQuery query = new FlairQuery(queryDto.interpret(), queryDto.isMetaRetrieved());

        FlairFactory flairFactory = queryAbstractFactory.getQueryFactory(connection.getConnectionType().getBundleClass());

        FlairCompiler compiler = flairFactory.getCompiler();

        QueryExecutor executor = flairFactory.getExecutor(connection);

        StringWriter writer = new StringWriter();

        try {
            compiler.compile(query, writer);
        } catch (CompilationException e) {
            e.printStackTrace();
        }

        log.info("Expected Query : {}", expectedQuery);
        log.info("Genrated Query : {}", writer.toString());


        assertThat(expectedQuery).isEqualToIgnoringCase(writer.toString());

    }


    @Test
    @Transactional
    public void subStrFunctionTest() throws Exception {


        String expectedQuery = "";

        // Create Query
        QueryDTO queryDto = new QueryDTO();

        queryDto.setFields(Arrays.asList("substr(product_name,0,2)"));

        queryDto.setSource("ecommerce");

        expectedQuery = "ecommerce.aggregate([{$project:{substr:{$substr: ['$product_name',0,2]}}}])";

        FlairQuery query = new FlairQuery(queryDto.interpret(), queryDto.isMetaRetrieved());

        FlairFactory flairFactory = queryAbstractFactory.getQueryFactory(connection.getConnectionType().getBundleClass());

        FlairCompiler compiler = flairFactory.getCompiler();

        QueryExecutor executor = flairFactory.getExecutor(connection);

        StringWriter writer = new StringWriter();

        try {
            compiler.compile(query, writer);
        } catch (CompilationException e) {
            e.printStackTrace();
        }

        log.info("Expected Query : {}", expectedQuery);
        log.info("Genrated Query : {}", writer.toString());


        assertThat(expectedQuery).isEqualToIgnoringCase(writer.toString());

    }


}
