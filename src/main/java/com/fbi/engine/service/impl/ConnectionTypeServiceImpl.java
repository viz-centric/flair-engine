package com.fbi.engine.service.impl;

import com.fbi.engine.domain.ConnectionType;
import com.fbi.engine.repository.ConnectionTypeRepository;
import com.fbi.engine.service.ConnectionTypeService;
import com.fbi.engine.service.dto.ConnectionTypeDTO;
import com.fbi.engine.service.mapper.ConnectionTypeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service Implementation for managing ConnectionType.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ConnectionTypeServiceImpl implements ConnectionTypeService{

    private final ConnectionTypeRepository connectionTypeRepository;

    private final ConnectionTypeMapper connectionTypeMapper;

    /**
     * Save a connectionType.
     *
     * @param connectionTypeDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public ConnectionTypeDTO save(ConnectionTypeDTO connectionTypeDTO) {
        log.debug("Request to save ConnectionType : {}", connectionTypeDTO);
        ConnectionType connectionType = connectionTypeMapper.toEntity(connectionTypeDTO);
        connectionType = connectionTypeRepository.save(connectionType);
        return connectionTypeMapper.toDto(connectionType);
    }

    /**
     *  Get all the connectionTypes.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ConnectionTypeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ConnectionTypes");
        return connectionTypeRepository.findAll(pageable)
            .map(connectionTypeMapper::toDto);
    }

    /**
     *  Get one connectionType by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public ConnectionTypeDTO findOne(Long id) {
        log.debug("Request to get ConnectionType : {}", id);
        ConnectionType connectionType = connectionTypeRepository.getOne(id);
        return connectionTypeMapper.toDto(connectionType);
    }

    /**
     *  Delete the  connectionType by id.
     *
     *  @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete ConnectionType : {}", id);
        connectionTypeRepository.deleteById(id);
    }
}
