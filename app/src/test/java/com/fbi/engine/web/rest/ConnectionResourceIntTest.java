package com.fbi.engine.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.fbi.engine.AbstractIntegrationTest;
import com.fbi.engine.domain.Connection;
import com.fbi.engine.domain.Driver;
import com.fbi.engine.domain.details.MySqlConnectionDetails;
import com.fbi.engine.repository.ConnectionRepository;
import com.fbi.engine.service.ConnectionService;
import com.fbi.engine.service.dto.ConnectionDTO;
import com.fbi.engine.service.mapper.ConnectionDTOTest;
import com.fbi.engine.service.mapper.ConnectionMapper;
import com.fbi.engine.web.rest.errors.ExceptionTranslator;

/**
 * Test class for the ConnectionResource REST controller.
 *
 * @see ConnectionResource
 */
public class ConnectionResourceIntTest extends AbstractIntegrationTest {

	private static final String DEFAULT_NAME = "AAAAAAAAAA";
	private static final String UPDATED_NAME = "BBBBBBBBBB";

	private static final String DEFAULT_CONNECTION_USERNAME = "AAAAAAAAAA";
	private static final String UPDATED_CONNECTION_USERNAME = "BBBBBBBBBB";

	private static final String DEFAULT_CONNECTION_PASSWORD = "AAAAAAAAAA";
	private static final String UPDATED_CONNECTION_PASSWORD = "BBBBBBBBBB";

	private static final String DEFAULT_LINK_ID = "AAAAAAAAAA";
	private static final String UPDATED_LINK_ID = "BBBBBBBBBB";

	@Autowired
	private ConnectionRepository connectionRepository;

	@Autowired
	private ConnectionMapper connectionMapper;

	@Autowired
	private ConnectionService connectionService;

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@Autowired
	private ExceptionTranslator exceptionTranslator;

	private MockMvc restConnectionMockMvc;

	private Connection connection;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
		ConnectionResource connectionResource = new ConnectionResource(connectionService);
		this.restConnectionMockMvc = MockMvcBuilders.standaloneSetup(connectionResource)
				.setCustomArgumentResolvers(pageableArgumentResolver).setControllerAdvice(exceptionTranslator)
				.setMessageConverters(jacksonMessageConverter).build();
	}

	/**
	 * Create an entity for this test.
	 * <p>
	 * This is a static method, as tests for other entities might also need it, if
	 * they test an entity which requires the current entity.
	 * 
	 * @param connName
	 * @param linkId
	 */
	public static Connection createEntity(String connName, String linkId) {
		MySqlConnectionDetails mysqlDetail = new MySqlConnectionDetails();
		mysqlDetail.setDatabaseName("services");
		mysqlDetail.setServerIp("localhost");
		mysqlDetail.setServerPort(3306);
		Connection connection = new Connection().name(connName).connectionUsername(DEFAULT_CONNECTION_USERNAME)
				.connectionPassword(DEFAULT_CONNECTION_PASSWORD).linkId(linkId);
		connection.setDetails(mysqlDetail);
		connection.setDriver(new Driver(new byte[] { 0 }, "test", "test", "test"));
		return connection;
	}

	@BeforeEach
	public void initTest() {
		connection = createEntity(DEFAULT_NAME, DEFAULT_LINK_ID);
	}

	@Test
	@Transactional
	public void createConnection() throws Exception {
		int databaseSizeBeforeCreate = connectionRepository.findAll().size();

		// Create the Connection

		ConnectionDTO connectionDTO = connectionMapper.toDto(connection);
		ConnectionDTOTest testConnection = new ConnectionDTOTest();
		testConnection.setConnectionPassword(connectionDTO.getConnectionPassword());
		testConnection.setConnectionType(connectionDTO.getConnectionType());
		testConnection.setConnectionUsername(connectionDTO.getConnectionUsername());
		testConnection.setDetails(connectionDTO.getDetails());
		testConnection.setName(connectionDTO.getName());
		testConnection.setDriver(connectionDTO.getDriver());

		restConnectionMockMvc.perform(post("/api/connections").contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(testConnection))).andExpect(status().isCreated());

		// Validate the Connection in the database
		List<Connection> connectionList = connectionRepository.findAll();
		assertThat(connectionList).hasSize(databaseSizeBeforeCreate + 1);
		Connection testConnection1 = connectionList.get(connectionList.size() - 1);
		assertThat(testConnection1.getName()).isEqualTo(DEFAULT_NAME);
		assertThat(testConnection1.getConnectionUsername()).isEqualTo(DEFAULT_CONNECTION_USERNAME);
	}

	@Test
	@Transactional
	public void createConnectionWithExistingId() throws Exception {
		int databaseSizeBeforeCreate = connectionRepository.findAll().size();

		// Create the Connection with an existing ID

		connection.setId(1L);
		ConnectionDTO connectionDTO = connectionMapper.toDto(connection);

		// An entity with an existing ID cannot be created, so this API call must fail
		restConnectionMockMvc.perform(post("/api/connections").contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(connectionDTO))).andExpect(status().isBadRequest());

		// Validate the Alice in the database
		List<Connection> connectionList = connectionRepository.findAll();
		assertThat(connectionList).hasSize(databaseSizeBeforeCreate);
	}

	@Test
	@Transactional
	public void checkNameIsRequired() throws Exception {
		int databaseSizeBeforeTest = connectionRepository.findAll().size();
		// set the field null
		connection.setName(null);

		// Create the Connection, which fails.
		ConnectionDTO connectionDTO = connectionMapper.toDto(connection);

		restConnectionMockMvc.perform(post("/api/connections").contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(connectionDTO))).andExpect(status().isBadRequest());

		List<Connection> connectionList = connectionRepository.findAll();
		assertThat(connectionList).hasSize(databaseSizeBeforeTest);
	}

	@Test
	@Transactional
	public void checkConnectionUsernameIsRequired() throws Exception {
		int databaseSizeBeforeTest = connectionRepository.findAll().size();
		// set the field null
		connection.setConnectionUsername(null);

		// Create the Connection, which fails.
		ConnectionDTO connectionDTO = connectionMapper.toDto(connection);

		restConnectionMockMvc.perform(post("/api/connections").contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(connectionDTO))).andExpect(status().isBadRequest());

		List<Connection> connectionList = connectionRepository.findAll();
		assertThat(connectionList).hasSize(databaseSizeBeforeTest);
	}

	@Test
	@Transactional
	public void checkConnectionPasswordIsRequired() throws Exception {
		int databaseSizeBeforeTest = connectionRepository.findAll().size();
		// set the field null
		connection.setConnectionPassword(null);

		// Create the Connection, which fails.
		ConnectionDTO connectionDTO = connectionMapper.toDto(connection);

		restConnectionMockMvc.perform(post("/api/connections").contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(connectionDTO))).andExpect(status().isBadRequest());

		List<Connection> connectionList = connectionRepository.findAll();
		assertThat(connectionList).hasSize(databaseSizeBeforeTest);
	}

	@Test
	@Transactional
	public void checkLinkIdIsRequired() throws Exception {
		int databaseSizeBeforeTest = connectionRepository.findAll().size();
		// set the field null
		connection.setLinkId(null);

		// Create the Connection, which fails.
		ConnectionDTO connectionDTO = connectionMapper.toDto(connection);

		restConnectionMockMvc.perform(post("/api/connections").contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(connectionDTO))).andExpect(status().isBadRequest());

		List<Connection> connectionList = connectionRepository.findAll();
		assertThat(connectionList).hasSize(databaseSizeBeforeTest);
	}

	@Test
	@Transactional
	public void getAllConnections() throws Exception {
		// Initialize the database
		connectionRepository.saveAndFlush(connection);

		// Get all the connectionList
		restConnectionMockMvc.perform(get("/api/connections?sort=id,desc")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.[*].id").value(hasItem(connection.getId().intValue())))
				.andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
				.andExpect(jsonPath("$.[*].connectionUsername").value(hasItem(DEFAULT_CONNECTION_USERNAME.toString())))
				.andExpect(jsonPath("$.[*].linkId").value(hasItem(connection.getLinkId())));
	}

	@Test
	@Transactional
	public void getAllConnectionsDoesNotReturnDeleted() throws Exception {
		// Initialize the database
		Connection savedConnection = connectionRepository.saveAndFlush(createEntity("customName", "customLink"));

		connectionRepository.delete(savedConnection);

		// Get all the connectionList
		restConnectionMockMvc.perform(get("/api/connections?sort=id,desc")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.[*].id").value(not(hasItem(savedConnection.getId().intValue()))));
	}

	@Test
	@Transactional
	public void getConnection() throws Exception {
		// Initialize the database
		connectionRepository.saveAndFlush(connection);

		// Get the connection
		restConnectionMockMvc.perform(get("/api/connections/{id}", connection.getId())).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.id").value(connection.getId().intValue()))
				.andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
				.andExpect(jsonPath("$.connectionUsername").value(DEFAULT_CONNECTION_USERNAME.toString()))
				.andExpect(jsonPath("$.linkId").value(connection.getLinkId()));
	}

	@Test
	@Transactional
	public void getNonExistingConnection() throws Exception {
		// Get the connection
		restConnectionMockMvc.perform(get("/api/connections/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
	}

	@Test
	@Transactional
	public void updateConnection() throws Exception {
		// Initialize the database
		connectionRepository.saveAndFlush(connection);
		int databaseSizeBeforeUpdate = connectionRepository.findAll().size();

		// Update the connection
		Connection updatedConnection = connectionRepository.getOne(connection.getId());
		updatedConnection.name(UPDATED_NAME).connectionUsername(UPDATED_CONNECTION_USERNAME)
				.connectionPassword(UPDATED_CONNECTION_PASSWORD).linkId(UPDATED_LINK_ID);
		ConnectionDTO connectionDTO = connectionMapper.toDto(updatedConnection);

		restConnectionMockMvc.perform(put("/api/connections").contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(connectionDTO))).andExpect(status().isOk());

		// Validate the Connection in the database
		List<Connection> connectionList = connectionRepository.findAll();
		assertThat(connectionList).hasSize(databaseSizeBeforeUpdate);
		Connection testConnection = connectionList.get(connectionList.size() - 1);
		assertThat(testConnection.getName()).isEqualTo(UPDATED_NAME);
		assertThat(testConnection.getConnectionUsername()).isEqualTo(UPDATED_CONNECTION_USERNAME);
		assertThat(testConnection.getConnectionPassword()).isEqualTo(UPDATED_CONNECTION_PASSWORD);
		assertThat(testConnection.getLinkId()).isEqualTo(UPDATED_LINK_ID);
	}

	@Test
	@Transactional
	public void updateNonExistingConnection() throws Exception {
		int databaseSizeBeforeUpdate = connectionRepository.findAll().size();

		// Create the Connection
		ConnectionDTO connectionDTO = connectionMapper.toDto(connection);

		// If the entity doesn't have an ID, it will be created instead of just being
		// updated
		restConnectionMockMvc.perform(put("/api/connections").contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(connectionDTO))).andExpect(status().isBadRequest());

		// Validate the Connection in the database
		List<Connection> connectionList = connectionRepository.findAll();
		assertThat(connectionList).hasSize(databaseSizeBeforeUpdate);
	}

	@Test
	@Transactional
	public void deleteConnection() throws Exception {
		// Initialize the database
		connectionRepository.saveAndFlush(connection);
		int databaseSizeBeforeDelete = connectionRepository.findAll().size();

		// Get the connection
		restConnectionMockMvc
				.perform(delete("/api/connections/{id}", connection.getId()).accept(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk());

		// Validate the database is empty
		List<Connection> connectionList = connectionRepository.findAll();
		assertThat(connectionList).hasSize(databaseSizeBeforeDelete - 1);
	}

	@Disabled
	@Test
	@Transactional
	public void equalsVerifier() throws Exception {
		TestUtil.equalsVerifier(Connection.class);
		Connection connection1 = new Connection();
		connection1.setId(1L);
		Connection connection2 = new Connection();
		connection2.setId(connection1.getId());
		assertThat(connection1).isEqualTo(connection2);
		connection2.setId(2L);
		assertThat(connection1).isNotEqualTo(connection2);
		connection1.setId(null);
		assertThat(connection1).isNotEqualTo(connection2);
	}

	@Test
	@Transactional
	public void dtoEqualsVerifier() throws Exception {
		TestUtil.equalsVerifier(ConnectionDTO.class);
		ConnectionDTO connectionDTO1 = new ConnectionDTO();
		connectionDTO1.setId(1L);
		ConnectionDTO connectionDTO2 = new ConnectionDTO();
		assertThat(connectionDTO1).isNotEqualTo(connectionDTO2);
		connectionDTO2.setId(connectionDTO1.getId());
		assertThat(connectionDTO1).isEqualTo(connectionDTO2);
		connectionDTO2.setId(2L);
		assertThat(connectionDTO1).isNotEqualTo(connectionDTO2);
		connectionDTO1.setId(null);
		assertThat(connectionDTO1).isNotEqualTo(connectionDTO2);
	}

	@Test
	@Transactional
	public void testEntityFromId() {
		assertThat(connectionMapper.fromId(42L).getId()).isEqualTo(42);
		assertThat(connectionMapper.fromId(null)).isNull();
	}
}
