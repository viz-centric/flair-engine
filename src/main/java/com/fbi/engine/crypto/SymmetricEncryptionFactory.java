package com.fbi.engine.crypto;

import com.fbi.engine.crypto.kdf.KeyDerivationFunction;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.TextEncryptor;

public interface SymmetricEncryptionFactory {

    /**
     * Instantiate symmetric cipher
     *
     * @param secretKey secret key represented in byte array
     * @return instance of {@link BytesEncryptor}
     */
    BytesEncryptor getSymmetricEncryption(byte[] secretKey);

    /**
     * Instantiate symmetric cipher
     *
     * @param passphrase password used for deriving key
     * @param salt       salt used for deriving key
     * @return instance of {@link BytesEncryptor}
     */
    BytesEncryptor getSymmetricEncryption(String passphrase, CharSequence salt, KeyDerivationFunction kdf);

    /**
     * Instantiate instance of symmetric {@link TextEncryptor}
     *
     * @param secretKey secret key represented in byte array
     * @return instance of {@link TextEncryptor}
     */
    TextEncryptor getSymmetricTextEncryption(byte[] secretKey);

    /**
     * Instantiate instance of symmetric {@link TextEncryptor}
     *
     * @param passphrase password used for deriving key
     * @param salt       salt used for deriving key
     * @return instance of {@link TextEncryptor}
     */
    TextEncryptor getSymmetricTextEncryption(String passphrase, CharSequence salt, KeyDerivationFunction kdf);


}
