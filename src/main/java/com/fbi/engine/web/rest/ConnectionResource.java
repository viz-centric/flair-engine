package com.fbi.engine.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.fbi.engine.domain.Connection;
import com.fbi.engine.service.ConnectionService;
import com.fbi.engine.service.dto.ConnectionDTO;
import com.fbi.engine.service.dto.UpdateConnectionDTO;
import com.fbi.engine.web.rest.util.HeaderUtil;
import com.querydsl.core.types.Predicate;
import io.github.jhipster.web.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
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

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Connection.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class ConnectionResource {

    private static final String ENTITY_NAME = "connection";

    private final ConnectionService connectionService;

    /**
     * POST  /connections : Create a new connection.
     *
     * @param connectionDTO the connectionDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new connectionDTO, or with status 400 (Bad Request) if the connection has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/connections")
    @Timed
    public ResponseEntity<ConnectionDTO> createConnection(@Valid @RequestBody ConnectionDTO connectionDTO) throws URISyntaxException {
        log.debug("REST request to save Connection : {}", connectionDTO);
        if (connectionDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new connection cannot already have an ID")).body(null);
        }
        ConnectionDTO result = connectionService.save(connectionDTO);
        return ResponseEntity.created(new URI("/api/connections/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /connections : Updates an existing connection.
     *
     * @param connectionDTO the connectionDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated connectionDTO,
     * or with status 400 (Bad Request) if the connectionDTO is not valid,
     * or with status 500 (Internal Server Error) if the connectionDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/connections")
    @Timed
    public ResponseEntity<ConnectionDTO> updateConnection(@Valid @RequestBody UpdateConnectionDTO connectionDTO) throws URISyntaxException {
        log.debug("REST request to update Connection : {}", connectionDTO);
        if (connectionDTO.getId() == null) {
            return ResponseEntity.badRequest().body(null);
        }
        ConnectionDTO result = connectionService.updateConnection(connectionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, connectionDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /connections/:id : get the "id" connection.
     *
     * @param id the id of the connectionDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the connectionDTO, or with status 404 (Not Found)
     */
    @GetMapping("/connections/{id}")
    @Timed
    public ResponseEntity<ConnectionDTO> getConnection(@PathVariable Long id) {
        log.debug("REST request to get Connection : {}", id);
        ConnectionDTO connectionDTO = connectionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(connectionDTO));
    }

    /**
     * DELETE  /connections/:id : delete the "id" connection.
     *
     * @param id the id of the connectionDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/connections/{id}")
    @Timed
    public ResponseEntity<Void> deleteConnection(@PathVariable Long id) {
        log.debug("REST request to delete Connection : {}", id);
        connectionService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * GET  /connections/all : get all the connections.
     *
     * @param predicate predicate
     * @return the ResponseEntity with status 200 (OK) and the list of connections in body
     */
    @GetMapping("/connections/all")
    @Timed
    public ResponseEntity<List<ConnectionDTO>> getAllConnections(@QuerydslPredicate(root = Connection.class) Predicate predicate) {
        log.debug("REST request to get a page of Connections");
        return new ResponseEntity<>(connectionService.findAllByRealm(predicate), HttpStatus.OK);
    }

}
