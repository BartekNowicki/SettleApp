package com.application.settleApp.services;

import com.application.settleApp.models.Cost;
import com.application.settleApp.repositories.CostRepository;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CostServiceImpl implements CostService {

  private final CostRepository costRepository;

  @Override
  public Cost findById(Long id) {
    return costRepository.findById(id).orElse(null);
  }

  @Override
  public Cost save(Cost object) {
    return costRepository.save(object);
  }

  @Override
  public Set<Cost> findAll() {
    return new HashSet<>(costRepository.findAll());
  }

  @Override
  public Cost delete(Cost object) {
    costRepository.delete(object);
    return object;
  }

  @Override
  public Long deleteById(Long id) {
    costRepository.deleteById(id);
    return id;
  }
}
