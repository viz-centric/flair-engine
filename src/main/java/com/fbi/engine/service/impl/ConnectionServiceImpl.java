package com.fbi.engine.service.impl;

import com.fbi.engine.domain.Connection;
import com.fbi.engine.repository.ConnectionRepository;
import com.fbi.engine.service.ConnectionService;
import com.fbi.engine.service.dto.ConnectionDTO;
import com.fbi.engine.service.dto.UpdateConnectionDTO;
import com.fbi.engine.service.mapper.ConnectionMapper;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Service Implementation for managing Connection.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ConnectionServiceImpl implements ConnectionService {

    private final ConnectionRepository connectionRepository;

    private final ConnectionMapper connectionMapper;

    /**
     * Save a connection.
     *
     * @param connectionDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public ConnectionDTO save(ConnectionDTO connectionDTO) {
        log.debug("Request to save Connection : {}", connectionDTO);
        Connection connection = connectionMapper.toEntity(connectionDTO);
        connection = connectionRepository.save(connection);
        return connectionMapper.toDto(connection);
    }

    /**
     * Get all the connections.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ConnectionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Connections");
        return connectionRepository.findAll(pageable)
            .map(connectionMapper::toDto);
    }

    /**
     * Get one connection by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public ConnectionDTO findOne(Long id) {
        log.debug("Request to get Connection : {}", id);
        Connection connection = connectionRepository.findOne(id);
        return connectionMapper.toDto(connection);
    }

    /**
     * Delete the  connection by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Connection : {}", id);
        connectionRepository.delete(id);
    }

    @Override
    public Connection findByConnectionName(String connectionName) {
        log.debug("Request to get connection by name: {}", connectionName);
        return connectionRepository.findByName(connectionName);
    }

    @Override
    public Connection findByConnectionLinkId(String linkId) {
        log.debug("Request to find connection by link id : {}", linkId);
        return connectionRepository.findByLinkId(linkId);
    }

    @Override
    public ConnectionDTO findByConnectionLinkIdAsDto(String linkId) {
        log.debug("Request to find connection by link id : {}", linkId);
        return connectionMapper.toDto(connectionRepository.findByLinkId(linkId));
    }

    @Override
    public List<ConnectionDTO> findAll(Predicate predicate) {
        log.debug("Request to get all Connections");
        return ((List<Connection>) connectionRepository.findAll(predicate))
            .stream()
            .map(connectionMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<Connection> findAll() {
        log.debug("Request to get all Connections");
        return connectionRepository.findAll();
    }

    @Override
    public List<ConnectionDTO> findAllAsDto() {
        log.debug("Request to get all Connections as dto");
        return connectionRepository.findAll()
            .stream()
            .map(connectionMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public ConnectionDTO updateConnection(UpdateConnectionDTO updateConnectionDTO) {
        final Connection connection = connectionRepository.findOne(updateConnectionDTO.getId());

        if (updateConnectionDTO.getConnectionPassword() != null) {
            connection.setConnectionPassword(updateConnectionDTO.getConnectionPassword());
        }
        connection.setConnectionUsername(updateConnectionDTO.getConnectionUsername());
        connection.setName(updateConnectionDTO.getName());
        connection.setDetails(updateConnectionDTO.getDetails());
        return connectionMapper.toDto(connectionRepository.save(connection));
    }
}
