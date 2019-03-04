package com.fbi.engine.crypto;

import com.fbi.engine.crypto.kdf.KdfType;
import com.fbi.engine.crypto.symmetric.SymmetricCipherType;

/**
 * Abstract factory that creates instances of {@link SymmetricEncryptionFactory}, {@link KeyDerivationFunctionFactory}
 */
public interface CryptoAbstractFactory {

    SymmetricEncryptionFactory getSymEncFactory();

    SymmetricEncryptionFactory getSymEncFactory(SymmetricCipherType type);

    KeyDerivationFunctionFactory getKdfFactory();

    KeyDerivationFunctionFactory getKdfFactory(KdfType type);
}
