package com.fbi.engine.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fbi.engine.service.dto.ConnectionTypeDTO;

/**
 * Service Interface for managing ConnectionType.
 */
public interface ConnectionTypeService {

	/**
	 * Save a connectionType.
	 *
	 * @param connectionTypeDTO the entity to save
	 * @return the persisted entity
	 */
	ConnectionTypeDTO save(ConnectionTypeDTO connectionTypeDTO);

	/**
	 * Get all the connectionTypes.
	 *
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	Page<ConnectionTypeDTO> findAll(Pageable pageable);

	/**
	 * Get the "id" connectionType.
	 *
	 * @param id the id of the entity
	 * @return the entity
	 */
	Optional<ConnectionTypeDTO> findOne(Long id);

	/**
	 * Delete the "id" connectionType.
	 *
	 * @param id the id of the entity
	 */
	void delete(Long id);
}
