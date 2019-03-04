package com.fbi.engine.crypto.kdf;

import org.bouncycastle.crypto.generators.SCrypt;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

/**
 * Implementation of {@link KeyDerivationFunction}
 * that uses SCrypt algorithm for key derivation
 */
public class SCryptKeyDerivationFunction implements KeyDerivationFunction {


    /**
     * Derive key from parameters
     *
     * @param passphrase passphrase used for deriving
     * @param salt       salt parameter
     * @param len        length of the key
     * @return key length of len specified
     */
    @Override
    public byte[] derive(String passphrase, byte[] salt, int len) {
        return SCrypt.generate(passphrase.getBytes(Charset.forName("UTF-8")), salt, 16384, 8, 8, len);
    }
}
