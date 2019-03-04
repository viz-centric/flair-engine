package com.fbi.engine.service;

import com.fbi.engine.domain.Connection;
import com.fbi.engine.service.dto.ConnectionDTO;
import com.fbi.engine.service.dto.UpdateConnectionDTO;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service Interface for managing Connection.
 */
public interface ConnectionService {

    /**
     * Save a connection.
     *
     * @param connectionDTO the entity to save
     * @return the persisted entity
     */
    ConnectionDTO save(ConnectionDTO connectionDTO);

    /**
     * Get all the connections.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<ConnectionDTO> findAll(Pageable pageable);

    /**
     * Get the "id" connection.
     *
     * @param id the id of the entity
     * @return the entity
     */
    ConnectionDTO findOne(Long id);

    /**
     * Delete the "id" connection.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    Connection findByConnectionName(String connectionName);

    Connection findByConnectionLinkId(String linkId);

    ConnectionDTO findByConnectionLinkIdAsDto(String linkId);

    List<ConnectionDTO> findAll(Predicate predicate);

    List<ConnectionDTO> findAllAsDto();

    ConnectionDTO updateConnection(UpdateConnectionDTO updateConnectionDTO);

    List<Connection> findAll();
}
