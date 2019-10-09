package com.fbi.engine.repository;

import com.fbi.engine.domain.ConnectionParameter;
import com.fbi.engine.domain.QConnectionParameter;
import com.querydsl.core.types.dsl.SimpleExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConnectionParameterRepository extends JpaRepository<ConnectionParameter, Long>, QuerydslPredicateExecutor<ConnectionParameter>,
        QuerydslBinderCustomizer<QConnectionParameter> {

    List<ConnectionParameter> findAllByLinkId(String linkId);

    @Modifying
    void deleteByLinkId(String linkId);

    /**
     * Customize the {@link QuerydslBindings} for the given root.
     *
     * @param bindings the {@link QuerydslBindings} to customize, will never be {@literal null}.
     * @param root     the entity root, will never be {@literal null}.
     */
    @Override
    default void customize(QuerydslBindings bindings, QConnectionParameter root) {
        bindings.bind(root.value).first(SimpleExpression::eq);
        bindings.bind(root.name).first(SimpleExpression::eq);
        bindings.bind(root.linkId).first(SimpleExpression::eq);
    }

}
