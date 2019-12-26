package com.fbi.engine.crypto.symmetric;

import com.fbi.engine.crypto.exceptions.IllegalCipherTextSizeException;
import com.fbi.engine.crypto.kdf.KeyDerivationFunction;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;

import static org.springframework.security.crypto.util.EncodingUtils.concatenate;
import static org.springframework.security.crypto.util.EncodingUtils.subArray;

/**
 * Implementation that follows
 * {@link org.springframework.security.crypto.encrypt.BouncyCastleAesGcmBytesEncryptor}
 * but enables use of other password based derivation functions and not just
 * PBKDF2.
 */
public final class AESGCMBytesEncryptor implements BytesEncryptor {

	private final KeyParameter secretKey;
	private final BytesKeyGenerator ivGenerator;

	public AESGCMBytesEncryptor(byte[] secretKey, BytesKeyGenerator ivGenerator) {
		this.secretKey = new KeyParameter(secretKey);
		this.ivGenerator = ivGenerator;
	}

	public AESGCMBytesEncryptor(String passphrase, KeyDerivationFunction keyDerivationFunction, CharSequence salt,
			BytesKeyGenerator ivGenerator) {
		this.secretKey = new KeyParameter(keyDerivationFunction.derive(passphrase, Hex.decode(salt), 32));
		this.ivGenerator = ivGenerator;
	}

	/**
	 * Encrypt the byte array.
	 *
	 * @param byteArray plain text
	 */
	@Override
	public byte[] encrypt(byte[] byteArray) {
		byte[] iv = this.ivGenerator.generateKey();

		GCMBlockCipher blockCipher = new GCMBlockCipher(new AESEngine());
		blockCipher.init(true, new AEADParameters(secretKey, 128, iv, null));

		byte[] encrypted = process(blockCipher, byteArray);

		return iv != null ? concatenate(iv, encrypted) : encrypted;
	}

	/**
	 * Decrypt the byte array.
	 *
	 * @param encryptedByteArray cipher text
	 */
	@Override
	public byte[] decrypt(byte[] encryptedByteArray) {

		if (encryptedByteArray.length <= this.ivGenerator.getKeyLength()) {
			throw new IllegalCipherTextSizeException();
		}

		byte[] iv = subArray(encryptedByteArray, 0, this.ivGenerator.getKeyLength());
		encryptedByteArray = subArray(encryptedByteArray, this.ivGenerator.getKeyLength(), encryptedByteArray.length);

		GCMBlockCipher blockCipher = new GCMBlockCipher(new AESEngine());
		blockCipher.init(false, new AEADParameters(secretKey, 128, iv, null));
		return process(blockCipher, encryptedByteArray);
	}

	private byte[] process(AEADBlockCipher blockCipher, byte[] in) {
		byte[] buf = new byte[blockCipher.getOutputSize(in.length)];
		int bytesWritten = blockCipher.processBytes(in, 0, in.length, buf, 0);
		try {
			bytesWritten += blockCipher.doFinal(buf, bytesWritten);
		} catch (InvalidCipherTextException e) {
			throw new IllegalStateException("unable to encrypt/decrypt", e);
		}
		if (bytesWritten == buf.length) {
			return buf;
		}
		byte[] out = new byte[bytesWritten];
		System.arraycopy(buf, 0, out, 0, bytesWritten);
		return out;
	}
}
