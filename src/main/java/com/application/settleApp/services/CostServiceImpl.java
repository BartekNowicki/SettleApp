package com.application.settleApp.services;

import com.application.settleApp.models.Cost;
import com.application.settleApp.models.Event;
import com.application.settleApp.models.User;
import com.application.settleApp.repositories.CostRepository;
import com.application.settleApp.repositories.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CostServiceImpl implements CostService {

  private final UserService userService;
  private final EventRepository eventRepository;
  private final CostRepository costRepository;

  @Override
  public Cost findById(Long id) {
    return costRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Cost not found with id: " + id));
  }

  @Override
  public Cost save(Cost cost) {
    return costRepository.save(cost);
  }

  public Cost save(Cost cost, Long relatedUserId, Long relatedEventId) {

    User user = userService.findById(relatedUserId);
    cost.setUser(user);

    Event event =
        eventRepository
            .findById(relatedEventId)
            .orElseThrow(
                () -> new EntityNotFoundException("Event not found with id: " + relatedEventId));
    cost.setEvent(event);

    return costRepository.save(cost);
  }

  @Override
  public List<Cost> findAll() {
    return costRepository.findAll();
  }

  @Override
  public Cost delete(Cost cost) {
    if (cost == null) {
      throw new IllegalArgumentException("Cannot delete a null cost.");
    }
    costRepository.delete(cost);
    return cost;
  }

  @Override
  public Cost deleteById(Long id) {
    Cost cost =
        costRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Cost not found with id: " + id));
    costRepository.delete(cost);

    return cost;
  }
}
