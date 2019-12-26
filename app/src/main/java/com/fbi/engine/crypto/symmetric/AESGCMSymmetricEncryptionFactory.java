package com.fbi.engine.crypto.symmetric;

import com.fbi.engine.crypto.SymmetricEncryptionFactory;
import com.fbi.engine.crypto.kdf.KeyDerivationFunction;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

public class AESGCMSymmetricEncryptionFactory implements SymmetricEncryptionFactory {

	/**
	 * Instantiate symmetric cipher
	 *
	 * @param secretKey secret key represented in byte array
	 * @return instance of {@link BytesEncryptor}
	 */
	@Override
	public BytesEncryptor getSymmetricEncryption(byte[] secretKey) {
		return new AESGCMBytesEncryptor(secretKey, KeyGenerators.secureRandom(16));
	}

	/**
	 * Instantiate symmetric cipher
	 *
	 * @param passphrase password used for deriving key
	 * @param salt       salt used for deriving key
	 * @return instance of {@link BytesEncryptor}
	 */
	@Override
	public BytesEncryptor getSymmetricEncryption(String passphrase, CharSequence salt, KeyDerivationFunction kdf) {
		return new AESGCMBytesEncryptor(passphrase, kdf, salt, KeyGenerators.secureRandom(16));
	}

	/**
	 * Instantiate instance of symmetric {@link TextEncryptor}
	 *
	 * @param secretKey secret key represented in byte array
	 * @return instance of {@link TextEncryptor}
	 */
	@Override
	public TextEncryptor getSymmetricTextEncryption(byte[] secretKey) {
		return new Base64EncodingTextEncryptor(getSymmetricEncryption(secretKey));
	}

	/**
	 * Instantiate instance of symmetric {@link TextEncryptor}
	 *
	 * @param passphrase password used for deriving key
	 * @param salt       salt used for deriving key
	 * @return instance of {@link TextEncryptor}
	 */
	@Override
	public TextEncryptor getSymmetricTextEncryption(String passphrase, CharSequence salt, KeyDerivationFunction kdf) {
		return new Base64EncodingTextEncryptor(getSymmetricEncryption(passphrase, salt, kdf));
	}

}
