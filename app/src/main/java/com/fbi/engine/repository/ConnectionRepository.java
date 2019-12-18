package com.fbi.engine.repository;

import com.fbi.engine.domain.Connection;
import com.fbi.engine.domain.QConnection;
import com.querydsl.core.types.dsl.SimpleExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data JPA repository for the Connection entity.
 */
@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long>, QueryDslPredicateExecutor<Connection>,
    QuerydslBinderCustomizer<QConnection> {

    Connection findByName(String username);

    @Override
    @Query("select c from Connection c where c.status is null or c.status <> com.fbi.engine.domain.ConnectionStatus.DELETED")
    List<Connection> findAll();

    @Override
    @Query("select c from Connection c where c.status is null or c.status <> com.fbi.engine.domain.ConnectionStatus.DELETED")
    Page<Connection> findAll(Pageable pageable);

    /**
     * Customize the {@link QuerydslBindings} for the given root.
     *
     * @param bindings the {@link QuerydslBindings} to customize, will never be {@literal null}.
     * @param root     the entity root, will never be {@literal null}.
     */
    @Override
    default void customize(QuerydslBindings bindings, QConnection root) {
        bindings.bind(root.connectionType.id).first(SimpleExpression::eq);
        bindings.bind(root.name).first(SimpleExpression::eq);
        bindings.bind(root.linkId).first(SimpleExpression::eq);
    }

    Connection findByLinkId(String linkId);

}
