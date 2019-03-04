package com.fbi.engine.crypto;


import com.fbi.engine.FbiengineApp;
import com.fbi.engine.crypto.converter.StringCryptoConverter;
import com.fbi.engine.crypto.exceptions.IllegalCipherTextSizeException;
import io.github.jhipster.config.JHipsterConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FbiengineApp.class)
public class StringCryptoConverterTest {

    private StringCryptoConverter converter;


    @Before
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


    @Test(expected = IllegalCipherTextSizeException.class)
    public void testDecryptInvalidCipherSize() {
        converter.convertToEntityAttribute(Base64Utils.encodeToString(new byte[]{0, 1, 0, 3, 1, 2, 3, 4, 1, 2, 2}));
    }

    @Test(expected = IllegalStateException.class)
    public void testDecryptInvalidCipher() {
        converter.convertToEntityAttribute("cmFuZG9tMTIzMTI1MTIzMTIzMTIzMTIzMjEzMTIzMTMxMjMxMjMxMjMyMTMyMQ==");
    }
}
