package org.example.dao;

import java.util.List;
import java.util.Optional;

/**
 * Generičko sučelje za CRUD operacije nad entitetima.
 * T = tip entiteta, ID = tip primarnog ključa
 */
public interface Repository<T, ID> {

    T save(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    void update(T entity);

    void deleteById(ID id);
}