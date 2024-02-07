package com.application.settleApp.services;

import java.util.Set;

public interface CrudService<T, ID> {
  T findById(ID id);

  T save(T object);

  Set<T> findAll();

  T delete(T object);

  ID deleteById(ID id);
}
