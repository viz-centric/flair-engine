package com.fbi.engine.repository;

import com.fbi.engine.domain.ConnectionType;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the ConnectionType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ConnectionTypeRepository extends JpaRepository<ConnectionType,Long> {
    
}
