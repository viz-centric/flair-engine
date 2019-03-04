package com.fbi.engine.web.rest;

import com.fbi.engine.crypto.CryptoAbstractFactory;
import com.fbi.engine.crypto.KeyManager;
import com.fbi.engine.crypto.SymmetricEncryptionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/crypto")
public class CryptoResource {

    private final CryptoAbstractFactory cryptoAbstractFactory;

    private final KeyManager keyManager;

    @PostMapping("/encrypt")
    public String encrypt(@RequestBody String data) {
        final SymmetricEncryptionFactory symEncFactory = cryptoAbstractFactory.getSymEncFactory();
        final TextEncryptor textEncryptor = symEncFactory.getSymmetricTextEncryption(keyManager.getDatabaseEncryptionKey());

        return textEncryptor.encrypt(data);
    }

    @PostMapping("/decrypt")
    public String decrypt(@RequestBody String cipherData) {
        final SymmetricEncryptionFactory symEncFactory = cryptoAbstractFactory.getSymEncFactory();
        final TextEncryptor textEncryptor = symEncFactory.getSymmetricTextEncryption(keyManager.getDatabaseEncryptionKey());
        return textEncryptor.decrypt(cipherData);
    }
}
