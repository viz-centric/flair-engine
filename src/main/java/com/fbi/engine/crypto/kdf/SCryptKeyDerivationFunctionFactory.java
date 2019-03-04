package com.fbi.engine.crypto.kdf;

import com.fbi.engine.crypto.KeyDerivationFunctionFactory;

public class SCryptKeyDerivationFunctionFactory implements KeyDerivationFunctionFactory {
    @Override
    public KeyDerivationFunction getInstance() {
        return new SCryptKeyDerivationFunction();
    }
}
