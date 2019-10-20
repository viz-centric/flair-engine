package com.fbi.engine.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.FbiengineApp;
import com.fbi.engine.crypto.converter.ConnectionDetailsCryptoConverter;
import com.fbi.engine.crypto.exceptions.IllegalCipherTextSizeException;
import com.fbi.engine.domain.details.ConnectionDetails;
import com.fbi.engine.domain.details.OracleConnectionDetails;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FbiengineApp.class)
public class ConnectionDetailsCryptoConverterTest {


    private ConnectionDetailsCryptoConverter converter;


    @Before
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


    @Test(expected = IllegalCipherTextSizeException.class)
    public void testDecryptInvalidCipherSize() {
        converter.convertToEntityAttribute(Base64Utils.encodeToString(new byte[]{0, 1, 0, 3, 1, 2, 3, 4, 1, 2, 2}));
    }

    @Test(expected = IllegalStateException.class)
    public void testDecryptInvalidCipher() {
        converter.convertToEntityAttribute("cmFuZG9tMTIzMTI1MTIzMTIzMTIzMTIzMjEzMTIzMTMxMjMxMjMxMjMyMTMyMQ==");
    }
}
