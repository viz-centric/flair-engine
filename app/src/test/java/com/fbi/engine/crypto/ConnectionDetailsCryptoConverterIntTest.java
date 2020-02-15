package com.fbi.engine.crypto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.Base64Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.AbstractIntegrationTest;
import com.fbi.engine.crypto.converter.ConnectionDetailsCryptoConverter;
import com.fbi.engine.crypto.exceptions.IllegalCipherTextSizeException;
import com.fbi.engine.domain.details.ConnectionDetails;
import com.fbi.engine.domain.details.OracleConnectionDetails;

public class ConnectionDetailsCryptoConverterIntTest extends AbstractIntegrationTest {

	private ConnectionDetailsCryptoConverter converter;

	@BeforeEach
	public void setup() {
		converter = new ConnectionDetailsCryptoConverter(new ObjectMapper());
	}

	/**
	 * Checks whether encrypted text can be again decrypted with following converter
	 */
	@Test
	public void testEncryptDecrypt() {
		OracleConnectionDetails details = new OracleConnectionDetails();
		details.setDatabaseName("test");
		details.setServerPort(2102);
		details.setServiceName("test-service");
		details.setServerIp("localhost");

		String cipherText = converter.convertToDatabaseColumn(details);

		ConnectionDetails details1 = converter.convertToEntityAttribute(cipherText);

		assertThat(details).isEqualTo(details1);
	}

	@Test
	public void testDecryptInvalidCipherSize() {
		assertThrows(IllegalCipherTextSizeException.class, () -> converter
				.convertToEntityAttribute(Base64Utils.encodeToString(new byte[] { 0, 1, 0, 3, 1, 2, 3, 4, 1, 2, 2 })));
	}

	@Test
	public void testDecryptInvalidCipher() {
		assertThrows(IllegalStateException.class, () -> converter
				.convertToEntityAttribute("cmFuZG9tMTIzMTI1MTIzMTIzMTIzMTIzMjEzMTIzMTMxMjMxMjMxMjMyMTMyMQ=="));
	}
}
