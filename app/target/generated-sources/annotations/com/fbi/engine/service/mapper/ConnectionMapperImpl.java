package com.fbi.engine.service.mapper;

import com.fbi.engine.domain.Connection;
import com.fbi.engine.service.dto.ConnectionDTO;
import com.fbi.engine.service.dto.UpdateConnectionDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2019-12-14T18:53:36+0100",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 1.8.0_232 (AdoptOpenJDK)"
)
@Component
public class ConnectionMapperImpl implements ConnectionMapper {

    @Autowired
    private ConnectionTypeMapper connectionTypeMapper;

    @Override
    public List<Connection> toEntity(List<ConnectionDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<Connection> list = new ArrayList<Connection>( dtoList.size() );
        for ( ConnectionDTO connectionDTO : dtoList ) {
            list.add( toEntity( connectionDTO ) );
        }

        return list;
    }

    @Override
    public List<ConnectionDTO> toDto(List<Connection> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<ConnectionDTO> list = new ArrayList<ConnectionDTO>( entityList.size() );
        for ( Connection connection : entityList ) {
            list.add( toDto( connection ) );
        }

        return list;
    }

    @Override
    public ConnectionDTO toDto(Connection connection) {
        if ( connection == null ) {
            return null;
        }

        ConnectionDTO connectionDTO = new ConnectionDTO();

        connectionDTO.setConnectionType( connectionTypeMapper.toDto( connection.getConnectionType() ) );
        connectionDTO.setId( connection.getId() );
        connectionDTO.setName( connection.getName() );
        connectionDTO.setConnectionUsername( connection.getConnectionUsername() );
        connectionDTO.setConnectionPassword( connection.getConnectionPassword() );
        connectionDTO.setLinkId( connection.getLinkId() );
        connectionDTO.setDetails( connection.getDetails() );
        connectionDTO.setStatus( connection.getStatus() );

        return connectionDTO;
    }

    @Override
    public Connection toEntity(ConnectionDTO connectionDTO) {
        if ( connectionDTO == null ) {
            return null;
        }

        Connection connection = new Connection();

        connection.setConnectionType( connectionTypeMapper.toEntity( connectionDTO.getConnectionType() ) );
        connection.setId( connectionDTO.getId() );
        connection.setName( connectionDTO.getName() );
        connection.setConnectionUsername( connectionDTO.getConnectionUsername() );
        connection.setConnectionPassword( connectionDTO.getConnectionPassword() );
        connection.setLinkId( connectionDTO.getLinkId() );
        connection.setDetails( connectionDTO.getDetails() );
        connection.setStatus( connectionDTO.getStatus() );

        return connection;
    }

    @Override
    public UpdateConnectionDTO toUDto(Connection connection) {
        if ( connection == null ) {
            return null;
        }

        UpdateConnectionDTO updateConnectionDTO = new UpdateConnectionDTO();

        updateConnectionDTO.setConnectionType( connectionTypeMapper.toDto( connection.getConnectionType() ) );
        updateConnectionDTO.setStatus( connection.getStatus() );
        updateConnectionDTO.setId( connection.getId() );
        updateConnectionDTO.setName( connection.getName() );
        updateConnectionDTO.setConnectionUsername( connection.getConnectionUsername() );
        updateConnectionDTO.setConnectionPassword( connection.getConnectionPassword() );
        updateConnectionDTO.setLinkId( connection.getLinkId() );
        updateConnectionDTO.setDetails( connection.getDetails() );

        return updateConnectionDTO;
    }

    @Override
    public Connection toEntity(UpdateConnectionDTO connectionDTO) {
        if ( connectionDTO == null ) {
            return null;
        }

        Connection connection = new Connection();

        connection.setConnectionType( connectionTypeMapper.toEntity( connectionDTO.getConnectionType() ) );
        connection.setId( connectionDTO.getId() );
        connection.setName( connectionDTO.getName() );
        connection.setConnectionUsername( connectionDTO.getConnectionUsername() );
        connection.setConnectionPassword( connectionDTO.getConnectionPassword() );
        connection.setLinkId( connectionDTO.getLinkId() );
        connection.setDetails( connectionDTO.getDetails() );
        connection.setStatus( connectionDTO.getStatus() );

        return connection;
    }
}
