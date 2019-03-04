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
import com.project.bi.query.expression.condition.impl.*;
import com.project.bi.query.expression.condition.impl.CompareConditionExpression.ComparatorType;
import org.junit.Before;
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

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FbiengineApp.class)
public class PostgresQueryTest {
	
		private Logger log = LoggerFactory.getLogger(getClass());

	    private static final String POSTGRE_ID = "1715917d-fff8-44a1-af02-ee2cd41a3609";

	    @Autowired
	    private ConnectionRepository connectionRepository;
	    
	    @Autowired
	    private QueryAbstractFactory queryAbstractFactory;
	    
	    private Connection connection;

	    @Before
	    public void setup() {
	        MockitoAnnotations.initMocks(this);
	        connection = connectionRepository.findByLinkId(POSTGRE_ID);
	    }


	    @Test
	    @Transactional
	    public void equalOperatorTest() throws Exception {
	    	
	    	
	    	List<String> operators=Arrays.asList("=",">","<","<=",">=","<>");
	    	
	    	String expectedQuery="";
	    	
	    	// Create Query
	        QueryDTO queryDto = new QueryDTO();
	       
	        queryDto.setFields(Arrays.asList("*"));
	        ConditionExpressionDTO expDto=new ConditionExpressionDTO();
	        
	        CompareConditionExpression copmareCdt=new CompareConditionExpression();
	        copmareCdt.setFeatureName("order_item_id");
	        copmareCdt.setValue("1");
	        
	    	
	    	for(String opr: operators) {
	    		
	    		copmareCdt.setComparatorType(TestUtil.getComparetorType(opr));
	    		
	    		expDto.setConditionExpression(copmareCdt);  
		        queryDto.setConditionExpressions(Arrays.asList(expDto));
		        queryDto.setSource("ecommerce");
	    		
	    		expectedQuery="select * from ecommerce where order_item_id "+opr+" 1";
	    
		        FlairQuery query = new FlairQuery();
		        query.setStatement(queryDto.interpret(connection.getName()));
		        query.setPullMeta(queryDto.isMetaRetrieved());
		       
		        FlairFactory flairFactory = queryAbstractFactory.getQueryFactory(connection.getConnectionType().getBundleClass());
		        
		        FlairCompiler compiler = flairFactory.getCompiler();


		        StringWriter writer = new StringWriter();

		        try {
		            compiler.compile(query, writer);
		        } catch (CompilationException e) {
		            e.printStackTrace();
		        }
		    
		        log.info("Expected Query : {}",expectedQuery);
		        log.info("Genrated Query : {}",writer.toString());
		        
		        assertThat(expectedQuery).isEqualToIgnoringCase(writer.toString());
	    		
	    	}
	    	
	    	
	        
	     //   retVal = queryService.executeQuery(connection, query);
	        

	    }
	    
	    
	    @Test
	    @Transactional
	    public void inOperatorTest() throws Exception {
	    	
	    	List<String> values=Arrays.asList("PENDING","PENDING_PAYMENT","COMPLETE");
	    	
	    	String expectedQuery="select * from ecommerce where order_status in ('PENDING')";
	        // Create Query
	        QueryDTO queryDto = new QueryDTO();
	       
	        
	        queryDto.setFields(Arrays.asList("*"));
	        ConditionExpressionDTO expDto=new ConditionExpressionDTO();
	        
	        ContainsConditionExpression containCdt=new ContainsConditionExpression();
	        containCdt.setFeatureName("order_status");
	        
	        containCdt.setValues(Arrays.asList("PENDING"));
	        
	        expDto.setConditionExpression(containCdt);
	        
	        queryDto.setConditionExpressions(Arrays.asList(expDto));
	        
	        queryDto.setSource("ecommerce");
	        
	        Connection connection = connectionRepository.findByLinkId(POSTGRE_ID);
	       
	        
	        FlairQuery query = new FlairQuery();
	        query.setStatement(queryDto.interpret(connection.getName()));
	        query.setPullMeta(queryDto.isMetaRetrieved());
	       
	        FlairFactory flairFactory = queryAbstractFactory.getQueryFactory(connection.getConnectionType().getBundleClass());
	        
	        FlairCompiler compiler = flairFactory.getCompiler();

	        QueryExecutor executor = flairFactory.getExecutor(connection);

	        StringWriter writer = new StringWriter();

	        try {
	            compiler.compile(query, writer);
	        } catch (CompilationException e) {
	            e.printStackTrace();
	        }
	    
	        log.info("Expected Query : {}",expectedQuery);
	        log.info("Genrated Query : {}",writer.toString());
	        
	        assertThat(expectedQuery).isEqualToIgnoringCase(writer.toString());
	        
	     //   retVal = queryService.executeQuery(connection, query);
	        

	    }
	    
	    @Test
	    @Transactional
	    public void orOperatorTest() throws Exception {
	    	
	    	
	    	List<String> operators=Arrays.asList("=",">","<","<=",">=","<>");
	    	
	    	String expectedQuery="";
	    	
	    	// Create Query
	        QueryDTO queryDto = new QueryDTO();
	       
	        queryDto.setFields(Arrays.asList("*"));
	        ConditionExpressionDTO expDto=new ConditionExpressionDTO();
	        
	        CompareConditionExpression copmareCdt1=new CompareConditionExpression();
	        copmareCdt1.setFeatureName("product_price");
	        copmareCdt1.setValue("500");
	        copmareCdt1.setComparatorType(ComparatorType.GTE);
	        
	        CompareConditionExpression copmareCdt2=new CompareConditionExpression();
	        copmareCdt2.setFeatureName("product_name");
	        copmareCdt2.setValue("Team Golf New England Patriots Putter Grip");
	        copmareCdt2.setComparatorType(ComparatorType.EQ);
	        
	        OrConditionExpression orCdt=new OrConditionExpression();
	        orCdt.setFirstExpression(copmareCdt1);
	        orCdt.setSecondExpression(copmareCdt2);
	        
	    	expDto.setConditionExpression(orCdt);  
		    queryDto.setConditionExpressions(Arrays.asList(expDto));
		    queryDto.setSource("ecommerce");
	    		
	    	expectedQuery="select * from ecommerce where product_price >= 500 or product_name = 'Team Golf New England Patriots Putter Grip'";
	    	     
		    FlairQuery query = new FlairQuery();
		    query.setStatement(queryDto.interpret(connection.getName()));
		    query.setPullMeta(queryDto.isMetaRetrieved());
		       
	        FlairFactory flairFactory = queryAbstractFactory.getQueryFactory(connection.getConnectionType().getBundleClass());
	        
	        FlairCompiler compiler = flairFactory.getCompiler();

	        QueryExecutor executor = flairFactory.getExecutor(connection);

	        StringWriter writer = new StringWriter();

	        try {
	            compiler.compile(query, writer);
	        } catch (CompilationException e) {
	            e.printStackTrace();
	        }
	    
	        log.info("Expected Query : {}",expectedQuery);
	        log.info("Genrated Query : {}",writer.toString());
	        
	        assertThat(expectedQuery).isEqualToIgnoringCase(writer.toString());
	    		
	    }
	    

	    @Test
	    @Transactional
	    public void andOperatorTest() throws Exception {
	    	
	    	
	    	List<String> operators=Arrays.asList("=",">","<","<=",">=","<>");
	    	
	    	String expectedQuery="";
	    	
	    	// Create Query
	        QueryDTO queryDto = new QueryDTO();
	       
	        queryDto.setFields(Arrays.asList("*"));
	        ConditionExpressionDTO expDto=new ConditionExpressionDTO();
	        
	        CompareConditionExpression copmareCdt1=new CompareConditionExpression();
	        copmareCdt1.setFeatureName("product_price");
	        copmareCdt1.setValue("500");
	        copmareCdt1.setComparatorType(ComparatorType.GTE);
	        
	        CompareConditionExpression copmareCdt2=new CompareConditionExpression();
	        copmareCdt2.setFeatureName("product_name");
	        copmareCdt2.setValue("Team Golf New England Patriots Putter Grip");
	        copmareCdt2.setComparatorType(ComparatorType.EQ);
	        
	        AndConditionExpression andCdt=new AndConditionExpression();
	        andCdt.setFirstExpression(copmareCdt1);
	        andCdt.setSecondExpression(copmareCdt2);
	        
	    	expDto.setConditionExpression(andCdt);  
		    queryDto.setConditionExpressions(Arrays.asList(expDto));
		    queryDto.setSource("ecommerce");
	    		
	    	expectedQuery="select * from ecommerce where product_price >= 500 and product_name = 'Team Golf New England Patriots Putter Grip'";
	    	     
		    FlairQuery query = new FlairQuery();
		    query.setStatement(queryDto.interpret(connection.getName()));
		    query.setPullMeta(queryDto.isMetaRetrieved());
		       
	        FlairFactory flairFactory = queryAbstractFactory.getQueryFactory(connection.getConnectionType().getBundleClass());
	        
	        FlairCompiler compiler = flairFactory.getCompiler();

	        QueryExecutor executor = flairFactory.getExecutor(connection);

	        StringWriter writer = new StringWriter();

	        try {
	            compiler.compile(query, writer);
	        } catch (CompilationException e) {
	            e.printStackTrace();
	        }
	    
	        log.info("Expected Query : {}",expectedQuery);
	        log.info("Genrated Query : {}",writer.toString());
	        
	        assertThat(expectedQuery).isEqualToIgnoringCase(writer.toString());
	    		
	    }
	    
	    @Test
	    @Transactional
	    public void betweenOperatorTest() throws Exception {
	    	
	    	
	    	String expectedQuery="";
	    	
	    	// Create Query
	        QueryDTO queryDto = new QueryDTO();
	       
	        queryDto.setFields(Arrays.asList("*"));
	        ConditionExpressionDTO expDto=new ConditionExpressionDTO();
	        
	        BetweenConditionExpression copmareCdt1=new BetweenConditionExpression();
	        copmareCdt1.setFeatureName("product_price");
	        copmareCdt1.setValue("500");
	        copmareCdt1.setSecondValue("1000");
	        
	    	expDto.setConditionExpression(copmareCdt1);  
		    queryDto.setConditionExpressions(Arrays.asList(expDto));
		    queryDto.setSource("ecommerce");
	    		
	    	expectedQuery="select * from ecommerce where product_price between 500 AND 1000";
	    	     
		    FlairQuery query = new FlairQuery();
		    query.setStatement(queryDto.interpret(connection.getName()));
		    query.setPullMeta(queryDto.isMetaRetrieved());
		       
	        FlairFactory flairFactory = queryAbstractFactory.getQueryFactory(connection.getConnectionType().getBundleClass());
	        
	        FlairCompiler compiler = flairFactory.getCompiler();

	        QueryExecutor executor = flairFactory.getExecutor(connection);

	        StringWriter writer = new StringWriter();

	        try {
	            compiler.compile(query, writer);
	        } catch (CompilationException e) {
	            e.printStackTrace();
	        }
	    
	        log.info("Expected Query : {}",expectedQuery);
	        log.info("Genrated Query : {}",writer.toString());
	        
	        assertThat(expectedQuery).isEqualToIgnoringCase(writer.toString());
	    		
	    }
	    
	    
	    @Test
	    @Transactional
	    public void likeOperatorTest() throws Exception {
	    	
	    	
	    	String expectedQuery="";
	    	
	    	// Create Query
	        QueryDTO queryDto = new QueryDTO();
	       
	        queryDto.setFields(Arrays.asList("*"));
	        ConditionExpressionDTO expDto=new ConditionExpressionDTO();
	        
	        LikeConditionExpression likeCdt=new LikeConditionExpression();
	        likeCdt.setFeatureName("product_name");
	        likeCdt.setValue("no");
	        
	    	expDto.setConditionExpression(likeCdt);  
		    queryDto.setConditionExpressions(Arrays.asList(expDto));
		    queryDto.setSource("ecommerce");
	    		
	    	expectedQuery="select * from ecommerce where product_name LIKE '%no%'";
	    	     
		    FlairQuery query = new FlairQuery();
		    query.setStatement(queryDto.interpret(connection.getName()));
		    query.setPullMeta(queryDto.isMetaRetrieved());
		       
	        FlairFactory flairFactory = queryAbstractFactory.getQueryFactory(connection.getConnectionType().getBundleClass());
	        
	        FlairCompiler compiler = flairFactory.getCompiler();

	        QueryExecutor executor = flairFactory.getExecutor(connection);

	        StringWriter writer = new StringWriter();

	        try {
	            compiler.compile(query, writer);
	        } catch (CompilationException e) {
	            e.printStackTrace();
	        }
	    
	        log.info("Expected Query : {}",expectedQuery);
	        log.info("Genrated Query : {}",writer.toString());
	        
	        assertThat(expectedQuery).isEqualToIgnoringCase(writer.toString());
	    		
	    }
	    
	    @Test
	    @Transactional
	    public void dateFunctionTest() throws Exception {
	    	
	    	
	    	String expectedQuery="";
	    	
	    	// Create Query
	        QueryDTO queryDto = new QueryDTO();
	       
	        queryDto.setFields(Arrays.asList("month(order_date) as month, year(order_date) as year, day(order_date) as day","hour(order_date) as hr","quarter(order_date) as qt","yearMonth(order_date) as ym", "yearWeek(order_date) as yw","yearQuarter(order_date) as yq"));
	       
		    queryDto.setSource("ecommerce");
	    		
	    	expectedQuery="SELECT date_part('month',order_date::timestamp) as month, date_part('year',order_date::timestamp) as year, date_part('day',order_date::timestamp) as day, date_part('hour',order_date::timestamp) as hr, date_part('quarter',order_date::timestamp) as qt, to_char(order_date,'YYYY-MM') as ym, to_char(order_date,'YYYY-WW') as yw, to_char(order_date,'YYYY-Q') as yq FROM ecommerce";
	    	     
		    FlairQuery query = new FlairQuery();
		    query.setStatement(queryDto.interpret(connection.getName()));
		    query.setPullMeta(queryDto.isMetaRetrieved());
		       
	        FlairFactory flairFactory = queryAbstractFactory.getQueryFactory(connection.getConnectionType().getBundleClass());
	        
	        FlairCompiler compiler = flairFactory.getCompiler();

	        QueryExecutor executor = flairFactory.getExecutor(connection);

	        StringWriter writer = new StringWriter();

	        try {
	            compiler.compile(query, writer);
	        } catch (CompilationException e) {
	            e.printStackTrace();
	        }
	    
	        log.info("Expected Query : {}",expectedQuery);
	        log.info("Genrated Query : {}",writer.toString());
	        
	        assertThat(expectedQuery).isEqualToIgnoringCase(writer.toString());
	    		
	    }
	    
	    
	    @Test
	    @Transactional
	    public void replaceFunctionTest() throws Exception {
	    	
	    	
	    	String expectedQuery="";
	    	
	    	// Create Query
	        QueryDTO queryDto = new QueryDTO();
	       
	        queryDto.setFields(Arrays.asList("replace(product_name,'Men','Women')"));
	       
		    queryDto.setSource("ecommerce");
	    		
	    	expectedQuery="SELECT replace(product_name,'Men','Women') FROM ecommerce";
	    	     
		    FlairQuery query = new FlairQuery();
		    query.setStatement(queryDto.interpret(connection.getName()));
		    query.setPullMeta(queryDto.isMetaRetrieved());
		       
	        FlairFactory flairFactory = queryAbstractFactory.getQueryFactory(connection.getConnectionType().getBundleClass());
	        
	        FlairCompiler compiler = flairFactory.getCompiler();

	        QueryExecutor executor = flairFactory.getExecutor(connection);

	        StringWriter writer = new StringWriter();

	        try {
	            compiler.compile(query, writer);
	        } catch (CompilationException e) {
	            e.printStackTrace();
	        }
	    
	        log.info("Expected Query : {}",expectedQuery);
	        log.info("Genrated Query : {}",writer.toString());
	        
	        assertThat(expectedQuery).isEqualToIgnoringCase(writer.toString());
	    		
	    }
	    
	    @Test
	    @Transactional
	    public void subStrFunctionTest() throws Exception {
	    	
	    	
	    	String expectedQuery="";
	    	
	    	// Create Query
	        QueryDTO queryDto = new QueryDTO();
	       
	        queryDto.setFields(Arrays.asList("substr(product_name,0,2)"));
	       
		    queryDto.setSource("ecommerce");
	    		
	    	expectedQuery="SELECT substr(product_name,0,2) FROM ecommerce";
	    	     
		    FlairQuery query = new FlairQuery();
		    query.setStatement(queryDto.interpret(connection.getName()));
		    query.setPullMeta(queryDto.isMetaRetrieved());
		       
	        FlairFactory flairFactory = queryAbstractFactory.getQueryFactory(connection.getConnectionType().getBundleClass());
	        
	        FlairCompiler compiler = flairFactory.getCompiler();

	        QueryExecutor executor = flairFactory.getExecutor(connection);

	        StringWriter writer = new StringWriter();

	        try {
	            compiler.compile(query, writer);
	        } catch (CompilationException e) {
	            e.printStackTrace();
	        }
	    
	        log.info("Expected Query : {}",expectedQuery);
	        log.info("Genrated Query : {}",writer.toString());
	        
	        
	        assertThat(expectedQuery).isEqualToIgnoringCase(writer.toString());
	    		
	    }
	    
	    
	    
	    @Test
	    @Transactional
	    public void mathFunctionTest() throws Exception {
	    	
	    	
	    	String expectedQuery="";
	    	
	    	// Create Query
	        QueryDTO queryDto = new QueryDTO();
	       
	        queryDto.setFields(Arrays.asList("count(*)","min(order_item_product_price)","max(order_item_product_price)","sum(order_item_product_price)"));
	       
		    queryDto.setSource("ecommerce");
	    		
	    	expectedQuery="SELECT count(*), min(order_item_product_price), max(order_item_product_price), sum(order_item_product_price) FROM ecommerce";
	    	     
		    FlairQuery query = new FlairQuery();
		    query.setStatement(queryDto.interpret(connection.getName()));
		    query.setPullMeta(queryDto.isMetaRetrieved());
		       
	        FlairFactory flairFactory = queryAbstractFactory.getQueryFactory(connection.getConnectionType().getBundleClass());
	        
	        FlairCompiler compiler = flairFactory.getCompiler();

	        QueryExecutor executor = flairFactory.getExecutor(connection);

	        StringWriter writer = new StringWriter();

	        try {
	            compiler.compile(query, writer);
	        } catch (CompilationException e) {
	            e.printStackTrace();
	        }
	    
	        log.info("Expected Query : {}",expectedQuery);
	        log.info("Genrated Query : {}",writer.toString());
	        
	        
	        assertThat(expectedQuery).isEqualToIgnoringCase(writer.toString());
	    		
	    }

	    
	    

}
