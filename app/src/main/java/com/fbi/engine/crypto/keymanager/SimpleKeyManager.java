package com.fbi.engine.crypto.keymanager;

import com.fbi.engine.ApplicationProperties;
import com.fbi.engine.crypto.CryptoAbstractFactory;
import com.fbi.engine.crypto.KeyDerivationFunctionFactory;
import com.fbi.engine.crypto.KeyManager;
import com.fbi.engine.crypto.kdf.KeyDerivationFunction;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@RefreshScope
@Component
@RequiredArgsConstructor
class SimpleKeyManager implements KeyManager {

    private final ApplicationProperties applicationProperties;

    private byte[] databaseEncryptionKey;

    private final CryptoAbstractFactory cryptoAbstractFactory;

    @PostConstruct
    public void postConstruct() {
        final KeyDerivationFunctionFactory kdfFactory = cryptoAbstractFactory.getKdfFactory();
        KeyDerivationFunction kdf = kdfFactory.getInstance();
        databaseEncryptionKey =
            kdf.derive(applicationProperties.getDatabase().getEncryption().getPassphrase(),
                Hex.decode(applicationProperties.getDatabase().getEncryption().getSalt()),
                applicationProperties.getDatabase().getEncryption().getKeyLength());
    }

    @Override
    public byte[] getDatabaseEncryptionKey() {
        return databaseEncryptionKey;
    }
}
