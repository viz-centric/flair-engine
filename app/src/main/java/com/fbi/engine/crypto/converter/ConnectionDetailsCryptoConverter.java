package com.fbi.engine.crypto.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.domain.details.ConnectionDetails;
import lombok.RequiredArgsConstructor;

import javax.persistence.Converter;
import java.io.IOException;

@Converter
@RequiredArgsConstructor
public class ConnectionDetailsCryptoConverter extends AbstractCryptoConverter<ConnectionDetails> {

    private final ObjectMapper objectMapper;

    public ConnectionDetailsCryptoConverter(){
        this(new ObjectMapper());
    }

    @Override
    ConnectionDetails stringToEntityAttribute(String dbData) {
        try {
            return this.objectMapper.readValue(dbData, ConnectionDetails.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    String entityAttributeToString(ConnectionDetails attribute) {
        try {
            return this.objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
