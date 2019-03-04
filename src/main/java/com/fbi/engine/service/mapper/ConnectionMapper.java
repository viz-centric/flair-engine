package com.fbi.engine.service.mapper;

import com.fbi.engine.domain.Connection;
import com.fbi.engine.service.dto.ConnectionDTO;
import com.fbi.engine.service.dto.UpdateConnectionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity Connection and its DTO ConnectionDTO.
 */
@Mapper(componentModel = "spring", uses = {ConnectionTypeMapper.class})
public interface ConnectionMapper extends EntityMapper<ConnectionDTO, Connection> {

    @Mapping(source = "connectionType", target = "connectionType")
    ConnectionDTO toDto(Connection connection);
    
    @Mapping(source = "connectionType", target = "connectionType")
    Connection toEntity(ConnectionDTO connectionDTO);

    @Mapping(source = "connectionType", target = "connectionType")
    UpdateConnectionDTO toUDto(Connection connection);

    @Mapping(source = "connectionType", target = "connectionType")
    Connection toEntity(UpdateConnectionDTO connectionDTO);

    default Connection fromId(Long id) {
        if (id == null) {
            return null;
        }
        Connection connection = new Connection();
        connection.setId(id);
        return connection;
    }
}
