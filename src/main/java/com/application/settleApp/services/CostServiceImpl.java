package com.application.settleApp.services;

import com.application.settleApp.enums.ExceptionMessage;
import com.application.settleApp.models.Cost;
import com.application.settleApp.models.Event;
import com.application.settleApp.models.User;
import com.application.settleApp.repositories.CostRepository;
import com.application.settleApp.repositories.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
        .orElseThrow(
            () -> new EntityNotFoundException(ExceptionMessage.COST_NOT_FOUND.getMessage() + id));
  }

  @Override
  public Cost save(Cost cost) {
    return costRepository.save(cost);
  }

  @Transactional
  public Cost save(Cost cost, Long relatedUserId, Long relatedEventId) {

    User user = userService.findById(relatedUserId);
    cost.setUser(user);
    user.addCost(cost);

    Event event =
        eventRepository
            .findById(relatedEventId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        ExceptionMessage.EVENT_NOT_FOUND.getMessage() + relatedEventId));
    cost.setEvent(event);
    event.addCost(cost);

    return costRepository.save(cost);
  }

  @Override
  public List<Cost> findAll() {
    return costRepository.findAll();
  }

  private void removeCostFromUserAndEvent(Cost cost) {
    if (cost.getUser() != null) {
      cost.getUser().getCosts().remove(cost);
    }
    if (cost.getEvent() != null) {
      cost.getEvent().getCosts().remove(cost);
    }
  }

  @Override
  @Transactional
  public Cost delete(Cost cost) {
    if (cost == null) {
      throw new IllegalArgumentException(ExceptionMessage.CANNOT_DELETE_NULL_COST.getMessage());
    }
    removeCostFromUserAndEvent(cost);
    costRepository.delete(cost);
    return cost;
  }

  @Override
  @Transactional
  public Cost deleteById(Long id) {
    Cost cost =
        costRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(ExceptionMessage.COST_NOT_FOUND.getMessage() + id));
    removeCostFromUserAndEvent(cost);
    costRepository.delete(cost);

    return cost;
  }
}
