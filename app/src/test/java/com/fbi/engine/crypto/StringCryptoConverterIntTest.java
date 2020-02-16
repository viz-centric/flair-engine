package com.fbi.engine.crypto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.Base64Utils;

import com.fbi.engine.AbstractIntegrationTest;
import com.fbi.engine.crypto.converter.StringCryptoConverter;
import com.fbi.engine.crypto.exceptions.IllegalCipherTextSizeException;

public class StringCryptoConverterIntTest extends AbstractIntegrationTest {

	private StringCryptoConverter converter;

	@BeforeEach
	public void setup() {
		converter = new StringCryptoConverter();
	}

	/**
	 * Checks whether encrypted text can be again decrypted with following converter
	 */
	@Test
	public void testEncryptDecrypt() {
		String cipherText = converter.convertToDatabaseColumn("text");

		String text = converter.convertToEntityAttribute(cipherText);

		assertThat(text).isEqualTo("text");
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
