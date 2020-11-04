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
import com.project.bi.query.dto.FieldDTO;
import com.project.bi.query.dto.QueryDTO;
import com.project.bi.query.expression.condition.impl.AndConditionExpression;
import com.project.bi.query.expression.condition.impl.BetweenConditionExpression;
import com.project.bi.query.expression.condition.impl.CompareConditionExpression;
import com.project.bi.query.expression.condition.impl.CompareConditionExpression.ComparatorType;
import com.project.bi.query.expression.condition.impl.ContainsConditionExpression;
import com.project.bi.query.expression.condition.impl.LikeConditionExpression;
import com.project.bi.query.expression.condition.impl.OrConditionExpression;
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
public class MySqlQueryTest {
	
		private Logger log = LoggerFactory.getLogger(getClass());

		private static final String MYSQL_ID = "7bbdbca5-b704-4d78-9ad4-0d3e2458ee5f";
	   
	    @Autowired
	    private ConnectionRepository connectionRepository;
 
	    @Autowired
	    private QueryAbstractFactory queryAbstractFactory;
	   
	    private Connection connection;

	    @Before
	    public void setup() {
	        MockitoAnnotations.initMocks(this);
	        connection = connectionRepository.findByLinkId(MYSQL_ID);
	    }


	    @Test
	    @Transactional
	    public void equalOperatorTest() throws Exception {
	    	
	    	
	    	List<String> operators=Arrays.asList("=",">","<","<=",">=","<>");
	    	
	    	String expectedQuery="";
	    	
	    	// Create Query
	        QueryDTO queryDto = new QueryDTO();
	       
	        queryDto.setFields(Arrays.asList(new FieldDTO("*")));
	        ConditionExpressionDTO expDto=new ConditionExpressionDTO();
	        
	        CompareConditionExpression copmareCdt=new CompareConditionExpression();
	        copmareCdt.setFeatureName("order_item_id");
	        copmareCdt.setValue("1");
	    	
	    	for(String opr: operators) {
	    		
	    		
	    		switch(opr) {
	    		
	    		case "=":	copmareCdt.setComparatorType(ComparatorType.EQ);
	    			break;
	    		case ">":	copmareCdt.setComparatorType(ComparatorType.GT);
	    			break;
	    		case "<":	copmareCdt.setComparatorType(ComparatorType.LT);
	    			break;
	    		case "<=":	copmareCdt.setComparatorType(ComparatorType.LTE);
	    			break;
	    		case ">=":	copmareCdt.setComparatorType(ComparatorType.GTE);
	    			break;
	    		case "<>":	copmareCdt.setComparatorType(ComparatorType.NEQ);
	    			break;
	    			
	    		}
	    		
	    		expDto.setConditionExpression(copmareCdt);  
		        queryDto.setConditionExpressions(Arrays.asList(expDto));
		        queryDto.setSource("ecommerce");
	    		
	    		expectedQuery="select * from ecommerce where order_item_id "+opr+" 1";
	    		
	    		
		        
		       
		       
		        String retVal="";

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
	       
	        
	        queryDto.setFields(Arrays.asList(new FieldDTO("*")));
	        ConditionExpressionDTO expDto=new ConditionExpressionDTO();
	        
	        ContainsConditionExpression containCdt=new ContainsConditionExpression();
	        containCdt.setFeatureName("order_status");
	        
	        containCdt.setValues(Arrays.asList("PENDING"));
	        
	        expDto.setConditionExpression(containCdt);
	        
	        queryDto.setConditionExpressions(Arrays.asList(expDto));
	        
	        queryDto.setSource("ecommerce");
	        
	        Connection connection = connectionRepository.findByLinkId(MYSQL_ID);


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
	       
	        queryDto.setFields(Arrays.asList(new FieldDTO("*")));
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
	    		
	    	expectedQuery="select * from ecommerce where (product_price >= 500 or product_name = 'Team Golf New England Patriots Putter Grip')";

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
	       
	        queryDto.setFields(Arrays.asList(new FieldDTO("*")));
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
	    		
	    	expectedQuery="select * from ecommerce where (product_price >= 500 and product_name = 'Team Golf New England Patriots Putter Grip')";

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
	    
	        log.info("Expected Query : {}",expectedQuery);
	        log.info("Genrated Query : {}",writer.toString());
	        
	        assertThat(expectedQuery).isEqualToIgnoringCase(writer.toString());
	    		
	    }
	    
	    @Test
	    @Transactional
	    public void OperatorTest() throws Exception {
	    	
	    	
	    	String expectedQuery="";
	    	
	    	// Create Query
	        QueryDTO queryDto = new QueryDTO();
	       
	        queryDto.setFields(Arrays.asList(new FieldDTO("*")));
	        ConditionExpressionDTO expDto=new ConditionExpressionDTO();
	        
	        BetweenConditionExpression copmareCdt1=new BetweenConditionExpression();
	        copmareCdt1.setFeatureName("product_price");
	        copmareCdt1.setValue("500");
	        copmareCdt1.setSecondValue("1000");
	        
	    	expDto.setConditionExpression(copmareCdt1);  
		    queryDto.setConditionExpressions(Arrays.asList(expDto));
		    queryDto.setSource("ecommerce");
	    		
	    	expectedQuery="select * from ecommerce where product_price between 500 AND 1000";

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
	       
	        queryDto.setFields(Arrays.asList(new FieldDTO("*")));
	        ConditionExpressionDTO expDto=new ConditionExpressionDTO();
	        
	        LikeConditionExpression likeCdt=new LikeConditionExpression();
	        likeCdt.setFeatureName("product_name");
	        likeCdt.setValue("no");
	        
	    	expDto.setConditionExpression(likeCdt);  
		    queryDto.setConditionExpressions(Arrays.asList(expDto));
		    queryDto.setSource("ecommerce");
	    		
	    	expectedQuery="select * from ecommerce where product_name LIKE '%no%'";

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
	       
	        queryDto.setFields(Arrays.asList(
					new FieldDTO("order_date", "month", "month"),
					new FieldDTO("order_date", "hour", "hr"),
					new FieldDTO("order_date", "quarter", "qt"),
					new FieldDTO("order_date", "yearMonth", "ym"),
					new FieldDTO("order_date", "yearWeek", "yw"),
					new FieldDTO("order_date", "yearQuarter", "yq")
			));
	       
		    queryDto.setSource("ecommerce");
	    		
	    	expectedQuery="SELECT EXTRACT(month FROM STR_TO_DATE(order_date,'%Y-%m-%d %H:%i:%s.%f')) as month, hour(order_date) as hr, EXTRACT(quarter FROM STR_TO_DATE(order_date,'%Y-%m-%d %H:%i:%s.%f')) as qt, CONCAT(EXTRACT(YEAR FROM STR_TO_DATE(order_date,'%Y-%m-%d %H:%i:%s.%f')), '-', EXTRACT(MONTH FROM STR_TO_DATE(order_date,'%Y-%m-%d %H:%i:%s.%f'))) as ym, CONCAT(EXTRACT(YEAR FROM STR_TO_DATE(order_date,'%Y-%m-%d %H:%i:%s.%f')), '-', EXTRACT(WEEK FROM STR_TO_DATE(order_date,'%Y-%m-%d %H:%i:%s.%f'))) as yw, CONCAT(EXTRACT(YEAR FROM STR_TO_DATE(order_date,'%Y-%m-%d %H:%i:%s.%f')), '-', EXTRACT(QUARTER FROM STR_TO_DATE(order_date,'%Y-%m-%d %H:%i:%s.%f'))) as yq FROM ecommerce";

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

			queryDto.setFields(Arrays.asList(new FieldDTO("Men"), new FieldDTO("Women")));
	       
		    queryDto.setSource("ecommerce");

			expectedQuery="SELECT Men, Women FROM ecommerce";

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
	       
	        queryDto.setFields(Arrays.asList(new FieldDTO("product_name", "substr")));
	       
		    queryDto.setSource("ecommerce");
	    		
	    	expectedQuery="SELECT substr(product_name) FROM ecommerce";

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
	       
	        queryDto.setFields(Arrays.asList(
					new FieldDTO("*", "count"),
					new FieldDTO("order_item_product_price", "min"),
					new FieldDTO("order_item_product_price", "max"),
					new FieldDTO("order_item_product_price", "sum")
			));
	       
		    queryDto.setSource("ecommerce");
	    		
	    	expectedQuery="SELECT count(*), min(order_item_product_price), max(order_item_product_price), sum(order_item_product_price) FROM ecommerce";

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
	    
	        log.info("Expected Query : {}",expectedQuery);
	        log.info("Genrated Query : {}",writer.toString());
	        
	        
	        assertThat(expectedQuery).isEqualToIgnoringCase(writer.toString());
	    		
	    }

	    

}
