package com.fbi.engine.crypto.converter;

import com.fbi.engine.crypto.CryptoAbstractFactory;
import com.fbi.engine.crypto.KeyManager;
import com.fbi.engine.crypto.SymmetricEncryptionFactory;
import com.fbi.engine.util.AutowireHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import javax.persistence.AttributeConverter;

public abstract class AbstractCryptoConverter<T> implements AttributeConverter<T, String> {

	@Autowired
	private CryptoAbstractFactory cryptoAbstractFactory;

	@Autowired
	private KeyManager keyManager;

	private void autowire() {
		if (cryptoAbstractFactory == null || keyManager == null) {
			AutowireHelper.autowire(this, this.cryptoAbstractFactory, this.keyManager);
		}

	}

	abstract T stringToEntityAttribute(String dbData);

	abstract String entityAttributeToString(T attribute);

	/**
	 * Converts the value stored in the entity attribute into the data
	 * representation to be stored in the database.
	 *
	 * @param attribute the entity attribute value to be converted
	 * @return the converted data to be stored in the database column
	 */
	@Override
	public String convertToDatabaseColumn(T attribute) {
		autowire();
		final SymmetricEncryptionFactory symEncFactory = cryptoAbstractFactory.getSymEncFactory();
		final TextEncryptor textEncryptor = symEncFactory
				.getSymmetricTextEncryption(keyManager.getDatabaseEncryptionKey());
		return textEncryptor.encrypt(entityAttributeToString(attribute));
	}

	/**
	 * Converts the data stored in the database column into the value to be stored
	 * in the entity attribute. Note that it is the responsibility of the converter
	 * writer to specify the correct dbData type for the corresponding column for
	 * use by the JDBC driver: i.e., persistence providers are not expected to do
	 * such type conversion.
	 *
	 * @param dbData the data from the database column to be converted
	 * @return the converted value to be stored in the entity attribute
	 */
	@Override
	public T convertToEntityAttribute(String dbData) {
		autowire();
		final SymmetricEncryptionFactory symEncFactory = cryptoAbstractFactory.getSymEncFactory();
		final TextEncryptor textEncryptor = symEncFactory
				.getSymmetricTextEncryption(keyManager.getDatabaseEncryptionKey());
		return stringToEntityAttribute(textEncryptor.decrypt(dbData));
	}
}
