package com.fbi.engine.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletSecurityElement;

import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.xnio.OptionMap;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlet.InstrumentedFilter;
import com.codahale.metrics.servlets.MetricsServlet;
import com.hazelcast.cardinality.CardinalityEstimator;
import com.hazelcast.config.Config;
import com.hazelcast.core.ClientService;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.DistributedObjectListener;
import com.hazelcast.core.Endpoint;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IAtomicReference;
import com.hazelcast.core.ICacheManager;
import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.IList;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ISemaphore;
import com.hazelcast.core.ISet;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.IdGenerator;
import com.hazelcast.core.LifecycleService;
import com.hazelcast.core.MultiMap;
import com.hazelcast.core.PartitionService;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.cp.CPSubsystem;
import com.hazelcast.crdt.pncounter.PNCounter;
import com.hazelcast.durableexecutor.DurableExecutorService;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import com.hazelcast.logging.LoggingService;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.quorum.QuorumService;
import com.hazelcast.ringbuffer.Ringbuffer;
import com.hazelcast.scheduledexecutor.IScheduledExecutorService;
import com.hazelcast.transaction.HazelcastXAResource;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.TransactionOptions;
import com.hazelcast.transaction.TransactionalTask;

import io.github.jhipster.config.JHipsterConstants;
import io.github.jhipster.config.JHipsterProperties;
import io.github.jhipster.web.filter.CachingHttpHeadersFilter;
import io.undertow.Undertow;
import io.undertow.Undertow.Builder;
import io.undertow.UndertowOptions;

/**
 * Unit tests for the WebConfigurer class.
 *
 * @see WebConfigurer
 */
public class WebConfigurerTest {

	private WebConfigurer webConfigurer;

	private MockServletContext servletContext;

	private MockEnvironment env;

	private JHipsterProperties props;

	private MetricRegistry metricRegistry;

	@Before
	public void setup() {
		servletContext = spy(new MockServletContext());
		doReturn(new MockFilterRegistration()).when(servletContext).addFilter(anyString(), any(Filter.class));
		doReturn(new MockServletRegistration()).when(servletContext).addServlet(anyString(), any(Servlet.class));

		env = new MockEnvironment();
		props = new JHipsterProperties();

		webConfigurer = new WebConfigurer(env, props);
	}

	@Test
	public void testStartUpProdServletContext() throws ServletException {
		env.setActiveProfiles(JHipsterConstants.SPRING_PROFILE_PRODUCTION);
		webConfigurer.onStartup(servletContext);

		assertThat(servletContext.getAttribute(InstrumentedFilter.REGISTRY_ATTRIBUTE)).isEqualTo(metricRegistry);
		assertThat(servletContext.getAttribute(MetricsServlet.METRICS_REGISTRY)).isEqualTo(metricRegistry);
		verify(servletContext).addFilter(eq("webappMetricsFilter"), any(InstrumentedFilter.class));
		verify(servletContext).addServlet(eq("metricsServlet"), any(MetricsServlet.class));
		verify(servletContext).addFilter(eq("cachingHttpHeadersFilter"), any(CachingHttpHeadersFilter.class));
	}

	@Test
	public void testStartUpDevServletContext() throws ServletException {
		env.setActiveProfiles(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT);
		webConfigurer.onStartup(servletContext);

		assertThat(servletContext.getAttribute(InstrumentedFilter.REGISTRY_ATTRIBUTE)).isEqualTo(metricRegistry);
		assertThat(servletContext.getAttribute(MetricsServlet.METRICS_REGISTRY)).isEqualTo(metricRegistry);
		verify(servletContext).addFilter(eq("webappMetricsFilter"), any(InstrumentedFilter.class));
		verify(servletContext).addServlet(eq("metricsServlet"), any(MetricsServlet.class));
		verify(servletContext, never()).addFilter(eq("cachingHttpHeadersFilter"), any(CachingHttpHeadersFilter.class));
	}

	@Test
	public void testCustomizeServletContainer() {
		env.setActiveProfiles(JHipsterConstants.SPRING_PROFILE_PRODUCTION);
		UndertowServletWebServerFactory container = new UndertowServletWebServerFactory();
		webConfigurer.customize(container);
		assertThat(container.getMimeMappings().get("abs")).isEqualTo("audio/x-mpeg");
		assertThat(container.getMimeMappings().get("html")).isEqualTo("text/html;charset=utf-8");
		assertThat(container.getMimeMappings().get("json")).isEqualTo("text/html;charset=utf-8");
		if (container.getDocumentRoot() != null) {
			assertThat(container.getDocumentRoot().getPath()).isEqualTo(FilenameUtils.separatorsToSystem("target/www"));
		}

		Builder builder = Undertow.builder();
		container.getBuilderCustomizers().forEach(c -> c.customize(builder));
		OptionMap.Builder serverOptions = (OptionMap.Builder) ReflectionTestUtils.getField(builder, "serverOptions");
		assertThat(serverOptions.getMap().get(UndertowOptions.ENABLE_HTTP2)).isNull();
	}

	@Test
	public void testUndertowHttp2Enabled() {
		props.getHttp().setVersion(JHipsterProperties.Http.Version.V_2_0);
		UndertowServletWebServerFactory container = new UndertowServletWebServerFactory();
		webConfigurer.customize(container);
		Builder builder = Undertow.builder();
		container.getBuilderCustomizers().forEach(c -> c.customize(builder));
		OptionMap.Builder serverOptions = (OptionMap.Builder) ReflectionTestUtils.getField(builder, "serverOptions");
		assertThat(serverOptions.getMap().get(UndertowOptions.ENABLE_HTTP2)).isTrue();
	}

	@Test
	public void testCorsFilterOnApiPath() throws Exception {
		props.getCors().setAllowedOrigins(Collections.singletonList("*"));
		props.getCors().setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
		props.getCors().setAllowedHeaders(Collections.singletonList("*"));
		props.getCors().setMaxAge(1800L);
		props.getCors().setAllowCredentials(true);

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new WebConfigurerTestController())
				.addFilters(webConfigurer.corsFilter()).build();

		mockMvc.perform(options("/api/test-cors").header(HttpHeaders.ORIGIN, "other.domain.com")
				.header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")).andExpect(status().isOk())
				.andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "other.domain.com"))
				.andExpect(header().string(HttpHeaders.VARY, "Origin"))
				.andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,PUT,DELETE"))
				.andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"))
				.andExpect(header().string(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "1800"));

		mockMvc.perform(get("/api/test-cors").header(HttpHeaders.ORIGIN, "other.domain.com")).andExpect(status().isOk())
				.andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "other.domain.com"));
	}

	@Test
	public void testCorsFilterOnOtherPath() throws Exception {
		props.getCors().setAllowedOrigins(Collections.singletonList("*"));
		props.getCors().setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
		props.getCors().setAllowedHeaders(Collections.singletonList("*"));
		props.getCors().setMaxAge(1800L);
		props.getCors().setAllowCredentials(true);

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new WebConfigurerTestController())
				.addFilters(webConfigurer.corsFilter()).build();

		mockMvc.perform(get("/test/test-cors").header(HttpHeaders.ORIGIN, "other.domain.com"))
				.andExpect(status().isOk()).andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
	}

	@Test
	public void testCorsFilterDeactivated() throws Exception {
		props.getCors().setAllowedOrigins(null);

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new WebConfigurerTestController())
				.addFilters(webConfigurer.corsFilter()).build();

		mockMvc.perform(get("/api/test-cors").header(HttpHeaders.ORIGIN, "other.domain.com")).andExpect(status().isOk())
				.andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
	}

	@Test
	public void testCorsFilterDeactivated2() throws Exception {
		props.getCors().setAllowedOrigins(new ArrayList<>());

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new WebConfigurerTestController())
				.addFilters(webConfigurer.corsFilter()).build();

		mockMvc.perform(get("/api/test-cors").header(HttpHeaders.ORIGIN, "other.domain.com")).andExpect(status().isOk())
				.andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
	}

	static class MockFilterRegistration implements FilterRegistration, FilterRegistration.Dynamic {

		@Override
		public void addMappingForServletNames(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter,
				String... servletNames) {

		}

		@Override
		public Collection<String> getServletNameMappings() {
			return null;
		}

		@Override
		public void addMappingForUrlPatterns(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter,
				String... urlPatterns) {

		}

		@Override
		public Collection<String> getUrlPatternMappings() {
			return null;
		}

		@Override
		public void setAsyncSupported(boolean isAsyncSupported) {

		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public String getClassName() {
			return null;
		}

		@Override
		public boolean setInitParameter(String name, String value) {
			return false;
		}

		@Override
		public String getInitParameter(String name) {
			return null;
		}

		@Override
		public Set<String> setInitParameters(Map<String, String> initParameters) {
			return null;
		}

		@Override
		public Map<String, String> getInitParameters() {
			return null;
		}
	}

	static class MockServletRegistration implements ServletRegistration, ServletRegistration.Dynamic {

		@Override
		public void setLoadOnStartup(int loadOnStartup) {

		}

		@Override
		public Set<String> setServletSecurity(ServletSecurityElement constraint) {
			return null;
		}

		@Override
		public void setMultipartConfig(MultipartConfigElement multipartConfig) {

		}

		@Override
		public void setRunAsRole(String roleName) {

		}

		@Override
		public void setAsyncSupported(boolean isAsyncSupported) {

		}

		@Override
		public Set<String> addMapping(String... urlPatterns) {
			return null;
		}

		@Override
		public Collection<String> getMappings() {
			return null;
		}

		@Override
		public String getRunAsRole() {
			return null;
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public String getClassName() {
			return null;
		}

		@Override
		public boolean setInitParameter(String name, String value) {
			return false;
		}

		@Override
		public String getInitParameter(String name) {
			return null;
		}

		@Override
		public Set<String> setInitParameters(Map<String, String> initParameters) {
			return null;
		}

		@Override
		public Map<String, String> getInitParameters() {
			return null;
		}
	}

	public static class MockHazelcastInstance implements HazelcastInstance {

		@Override
		public String getName() {
			return "HazelcastInstance";
		}

		@Override
		public <E> IQueue<E> getQueue(String s) {
			return null;
		}

		@Override
		public <E> ITopic<E> getTopic(String s) {
			return null;
		}

		@Override
		public <E> ISet<E> getSet(String s) {
			return null;
		}

		@Override
		public <E> IList<E> getList(String s) {
			return null;
		}

		@Override
		public <K, V> IMap<K, V> getMap(String s) {
			return null;
		}

		@Override
		public <K, V> ReplicatedMap<K, V> getReplicatedMap(String s) {
			return null;
		}

		@Override
		public JobTracker getJobTracker(String s) {
			return null;
		}

		@Override
		public <K, V> MultiMap<K, V> getMultiMap(String s) {
			return null;
		}

		@Override
		public ILock getLock(String s) {
			return null;
		}

		@Override
		public <E> Ringbuffer<E> getRingbuffer(String s) {
			return null;
		}

		@Override
		public <E> ITopic<E> getReliableTopic(String s) {
			return null;
		}

		@Override
		public Cluster getCluster() {
			return null;
		}

		@Override
		public Endpoint getLocalEndpoint() {
			return null;
		}

		@Override
		public IExecutorService getExecutorService(String s) {
			return null;
		}

		@Override
		public DurableExecutorService getDurableExecutorService(String s) {
			return null;
		}

		@Override
		public <T> T executeTransaction(TransactionalTask<T> transactionalTask) throws TransactionException {
			return null;
		}

		@Override
		public <T> T executeTransaction(TransactionOptions transactionOptions, TransactionalTask<T> transactionalTask)
				throws TransactionException {
			return null;
		}

		@Override
		public TransactionContext newTransactionContext() {
			return null;
		}

		@Override
		public TransactionContext newTransactionContext(TransactionOptions transactionOptions) {
			return null;
		}

		@Override
		public IdGenerator getIdGenerator(String s) {
			return null;
		}

		@Override
		public IAtomicLong getAtomicLong(String s) {
			return null;
		}

		@Override
		public <E> IAtomicReference<E> getAtomicReference(String s) {
			return null;
		}

		@Override
		public ICountDownLatch getCountDownLatch(String s) {
			return null;
		}

		@Override
		public ISemaphore getSemaphore(String s) {
			return null;
		}

		@Override
		public Collection<DistributedObject> getDistributedObjects() {
			return null;
		}

		@Override
		public String addDistributedObjectListener(DistributedObjectListener distributedObjectListener) {
			return null;
		}

		@Override
		public boolean removeDistributedObjectListener(String s) {
			return false;
		}

		@Override
		public Config getConfig() {
			return null;
		}

		@Override
		public PartitionService getPartitionService() {
			return null;
		}

		@Override
		public QuorumService getQuorumService() {
			return null;
		}

		@Override
		public ClientService getClientService() {
			return null;
		}

		@Override
		public LoggingService getLoggingService() {
			return null;
		}

		@Override
		public LifecycleService getLifecycleService() {
			return null;
		}

		@Override
		public <T extends DistributedObject> T getDistributedObject(String s, String s1) {
			return null;
		}

		@Override
		public ConcurrentMap<String, Object> getUserContext() {
			return null;
		}

		@Override
		public HazelcastXAResource getXAResource() {
			return null;
		}

		@Override
		public ICacheManager getCacheManager() {
			return null;
		}

		@Override
		public void shutdown() {

		}

		@Override
		public FlakeIdGenerator getFlakeIdGenerator(String name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CardinalityEstimator getCardinalityEstimator(String name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public PNCounter getPNCounter(String name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IScheduledExecutorService getScheduledExecutorService(String name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CPSubsystem getCPSubsystem() {
			// TODO Auto-generated method stub
			return null;
		}
	}

}
