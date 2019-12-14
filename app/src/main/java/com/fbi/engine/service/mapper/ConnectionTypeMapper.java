package com.fbi.engine.service.mapper;

import com.fbi.engine.domain.*;
import com.fbi.engine.service.dto.ConnectionTypeDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity ConnectionType and its DTO ConnectionTypeDTO.
 */
@Mapper(componentModel = "spring")
public interface ConnectionTypeMapper extends EntityMapper<ConnectionTypeDTO, ConnectionType> {


    default ConnectionType fromId(Long id) {
        if (id == null) {
            return null;
        }
        ConnectionType connectionType = new ConnectionType();
        connectionType.setId(id);
        return connectionType;
    }
}
