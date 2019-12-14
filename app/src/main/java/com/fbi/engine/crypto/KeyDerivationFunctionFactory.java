package com.fbi.engine.crypto;

import com.fbi.engine.crypto.kdf.KeyDerivationFunction;

public interface KeyDerivationFunctionFactory {

    KeyDerivationFunction getInstance();

}
