package com.fbi.engine.crypto;

/**
 * Manages loaded keys of the application
 */
public interface KeyManager {

    /**
     * Retrieve database encryption key used for encrypting database data
     *
     * @return byte array representing secret key
     */
    byte[] getDatabaseEncryptionKey();
}
