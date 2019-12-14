package com.fbi.engine.service.mapper;

import com.fbi.engine.domain.ConnectionType;
import com.fbi.engine.service.dto.ConnectionTypeDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2019-12-14T18:53:36+0100",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 1.8.0_232 (AdoptOpenJDK)"
)
@Component
public class ConnectionTypeMapperImpl implements ConnectionTypeMapper {

    @Override
    public ConnectionType toEntity(ConnectionTypeDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ConnectionType connectionType = new ConnectionType();

        connectionType.setId( dto.getId() );
        connectionType.setName( dto.getName() );
        connectionType.setBundleClass( dto.getBundleClass() );
        connectionType.setConnectionPropertiesSchema( dto.getConnectionPropertiesSchema() );

        return connectionType;
    }

    @Override
    public ConnectionTypeDTO toDto(ConnectionType entity) {
        if ( entity == null ) {
            return null;
        }

        ConnectionTypeDTO connectionTypeDTO = new ConnectionTypeDTO();

        connectionTypeDTO.setId( entity.getId() );
        connectionTypeDTO.setName( entity.getName() );
        connectionTypeDTO.setBundleClass( entity.getBundleClass() );
        connectionTypeDTO.setConnectionPropertiesSchema( entity.getConnectionPropertiesSchema() );

        return connectionTypeDTO;
    }

    @Override
    public List<ConnectionType> toEntity(List<ConnectionTypeDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<ConnectionType> list = new ArrayList<ConnectionType>( dtoList.size() );
        for ( ConnectionTypeDTO connectionTypeDTO : dtoList ) {
            list.add( toEntity( connectionTypeDTO ) );
        }

        return list;
    }

    @Override
    public List<ConnectionTypeDTO> toDto(List<ConnectionType> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<ConnectionTypeDTO> list = new ArrayList<ConnectionTypeDTO>( entityList.size() );
        for ( ConnectionType connectionType : entityList ) {
            list.add( toDto( connectionType ) );
        }

        return list;
    }
}
