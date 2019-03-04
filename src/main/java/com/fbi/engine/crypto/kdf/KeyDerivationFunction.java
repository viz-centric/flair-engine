package com.fbi.engine.crypto.kdf;

/**
 * Service that is used to derive key from given passphrase
 */
public interface KeyDerivationFunction {

    /**
     * Derive key from parameters
     *
     * @param passphrase passphrase used for deriving
     * @param salt       seed parameter
     * @param len        length of the key
     * @return key length of len specified
     */
    byte[] derive(String passphrase, byte[] salt, int len);
}
