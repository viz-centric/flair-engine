package com.fbi.engine.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fbi.engine.service.ConnectionTypeService;
import com.fbi.engine.service.dto.ConnectionTypeDTO;
import com.fbi.engine.web.rest.util.HeaderUtil;
import com.fbi.engine.web.rest.util.PaginationUtil;

import io.github.jhipster.web.util.ResponseUtil;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for managing ConnectionType.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ConnectionTypeResource {

	private static final String ENTITY_NAME = "connectionType";

	private final ConnectionTypeService connectionTypeService;

	/**
	 * POST /connection-types : Create a new connectionType.
	 *
	 * @param connectionTypeDTO the connectionTypeDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the new
	 *         connectionTypeDTO, or with status 400 (Bad Request) if the
	 *         connectionType has already an ID
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@PostMapping("/connection-types")
	public ResponseEntity<ConnectionTypeDTO> createConnectionType(
			@Valid @RequestBody ConnectionTypeDTO connectionTypeDTO) throws URISyntaxException {
		log.debug("REST request to save ConnectionType : {}", connectionTypeDTO);
		if (connectionTypeDTO.getId() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists",
					"A new connectionType cannot already have an ID")).body(null);
		}
		ConnectionTypeDTO result = connectionTypeService.save(connectionTypeDTO);
		return ResponseEntity.created(new URI("/api/connection-types/" + result.getId()))
				.headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString())).body(result);
	}

	/**
	 * PUT /connection-types : Updates an existing connectionType.
	 *
	 * @param connectionTypeDTO the connectionTypeDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         connectionTypeDTO, or with status 400 (Bad Request) if the
	 *         connectionTypeDTO is not valid, or with status 500 (Internal Server
	 *         Error) if the connectionTypeDTO couldn't be updated
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@PutMapping("/connection-types")
	public ResponseEntity<ConnectionTypeDTO> updateConnectionType(
			@Valid @RequestBody ConnectionTypeDTO connectionTypeDTO) throws URISyntaxException {
		log.debug("REST request to update ConnectionType : {}", connectionTypeDTO);
		if (connectionTypeDTO.getId() == null) {
			return createConnectionType(connectionTypeDTO);
		}
		ConnectionTypeDTO result = connectionTypeService.save(connectionTypeDTO);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, connectionTypeDTO.getId().toString()))
				.body(result);
	}

	/**
	 * GET /connection-types : get all the connectionTypes.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         connectionTypes in body
	 */
	@GetMapping("/connection-types")
	public ResponseEntity<List<ConnectionTypeDTO>> getAllConnectionTypes(@ApiParam Pageable pageable) {
		log.debug("REST request to get a page of ConnectionTypes");
		Page<ConnectionTypeDTO> page = connectionTypeService.findAll(pageable);
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/connection-types");
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	/**
	 * GET /connection-types/:id : get the "id" connectionType.
	 *
	 * @param id the id of the connectionTypeDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         connectionTypeDTO, or with status 404 (Not Found)
	 */
	@GetMapping("/connection-types/{id}")
	public ResponseEntity<ConnectionTypeDTO> getConnectionType(@PathVariable Long id) {
		log.debug("REST request to get ConnectionType : {}", id);
		ConnectionTypeDTO connectionTypeDTO = connectionTypeService.findOne(id);
		return ResponseUtil.wrapOrNotFound(Optional.ofNullable(connectionTypeDTO));
	}

	/**
	 * DELETE /connection-types/:id : delete the "id" connectionType.
	 *
	 * @param id the id of the connectionTypeDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@DeleteMapping("/connection-types/{id}")
	public ResponseEntity<Void> deleteConnectionType(@PathVariable Long id) {
		log.debug("REST request to delete ConnectionType : {}", id);
		connectionTypeService.delete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
	}
}
