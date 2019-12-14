package com.fbi.engine.crypto.converter;

import javax.persistence.Converter;

@Converter
public class StringCryptoConverter extends AbstractCryptoConverter<String> {

    @Override
    String stringToEntityAttribute(String dbData) {
        return dbData;
    }

    @Override
    String entityAttributeToString(String attribute) {
        return attribute;
    }
}
