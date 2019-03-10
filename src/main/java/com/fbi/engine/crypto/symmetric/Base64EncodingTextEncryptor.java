package com.fbi.engine.crypto.symmetric;

import lombok.RequiredArgsConstructor;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.TextEncryptor;

/**
 * Implementation of {@link TextEncryptor}
 * that encodes and decodes data in Base64
 */
@RequiredArgsConstructor
public class Base64EncodingTextEncryptor implements TextEncryptor {

    private final BytesEncryptor bytesEncryptor;

    /**
     * Encrypt the raw text string.
     *
     * @param text text
     */
    @Override
    public String encrypt(String text) {
        return Base64.toBase64String(bytesEncryptor.encrypt(Utf8.encode(text)));
    }

    /**
     * Decrypt the encrypted text string.
     *
     * @param encryptedText text
     */
    @Override
    public String decrypt(String encryptedText) {
        return Utf8.decode(bytesEncryptor.decrypt(Base64.decode(encryptedText)));
    }
}
