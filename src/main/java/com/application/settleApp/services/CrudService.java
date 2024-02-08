package com.application.settleApp.services;

import java.util.List;

public interface CrudService<T, ID> {
  T findById(ID id);

  T save(T object);

  List<T> findAll();

  T delete(T object);

  T deleteById(ID id);
}
