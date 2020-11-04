package com.fbi.engine.service;

import com.fbi.engine.domain.Connection;
import com.fbi.engine.service.dto.ConnectionDTO;
import com.fbi.engine.service.dto.UpdateConnectionDTO;
import com.querydsl.core.types.Predicate;

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

    Connection findByConnectionLinkId(String linkId);

    ConnectionDTO findById(Long id);

    ConnectionDTO findByConnectionLinkIdAsDto(String linkId);

    List<ConnectionDTO> findAllByRealm(Predicate predicate);

    ConnectionDTO updateConnection(UpdateConnectionDTO updateConnectionDTO);
}
