package com.fbi.engine.crypto.abstractfactory;

import com.fbi.engine.ApplicationProperties;
import com.fbi.engine.crypto.CryptoAbstractFactory;
import com.fbi.engine.crypto.KeyDerivationFunctionFactory;
import com.fbi.engine.crypto.SymmetricEncryptionFactory;
import com.fbi.engine.crypto.kdf.KdfType;
import com.fbi.engine.crypto.kdf.SCryptKeyDerivationFunctionFactory;
import com.fbi.engine.crypto.symmetric.AESGCMSymmetricEncryptionFactory;
import com.fbi.engine.crypto.symmetric.SymmetricCipherType;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@RefreshScope
@Component
@RequiredArgsConstructor
class CryptoAbstractFactoryImpl implements CryptoAbstractFactory {

    private final ApplicationProperties applicationProperties;

    @Override
    public SymmetricEncryptionFactory getSymEncFactory() {
        return getSymEncFactory(applicationProperties.getConfiguration().getCrypto().getSymmetricCipherType());
    }

    @Override
    public SymmetricEncryptionFactory getSymEncFactory(SymmetricCipherType type) {
        switch (type) {
            case AES_256_GCM:
                return new AESGCMSymmetricEncryptionFactory();
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public KeyDerivationFunctionFactory getKdfFactory() {
        return getKdfFactory(applicationProperties.getConfiguration().getCrypto().getKdfType());
    }

    @Override
    public KeyDerivationFunctionFactory getKdfFactory(KdfType type) {
        switch (type) {
            case SCRYPT:
                return new SCryptKeyDerivationFunctionFactory();
            default:
                throw new IllegalArgumentException();
        }
    }
}
