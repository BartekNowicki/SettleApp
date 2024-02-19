package com.application.settleApp.services;

import com.application.settleApp.model.Cost;
import com.application.settleApp.model.Event;
import com.application.settleApp.model.User;
import com.application.settleApp.repositories.EventRepository;
import com.application.settleApp.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EventServiceImpl implements EventService {

  private final EventRepository eventRepository;
  private final UserRepository userRepository;
  private final UserServiceImpl userService;
  private final CostServiceImpl costService;

  public Event findById(Long id) {
    return eventRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + id));
  }

  @Override
  public Event save(Event event) {
    return eventRepository.save(event);
  }

  @Transactional
  public Event save(Event event, Set<Long> relatedParticipantIds, Set<Long> relatedProductIds) {

    if (relatedParticipantIds != null && !relatedParticipantIds.isEmpty()) {
      Set<User> participants =
          relatedParticipantIds.stream()
              .map(participantId -> userService.findById(participantId))
              .collect(Collectors.toSet());
      participants.forEach(participant -> participant.addEvent(event));
      event.setParticipants(participants);
    }

    if (relatedProductIds != null && !relatedProductIds.isEmpty()) {
      Set<Cost> costs =
          relatedProductIds.stream()
              .map(productId -> costService.findById(productId))
              .collect(Collectors.toSet());
      costs.forEach(cost -> cost.setEvent(event));
      event.setCosts(costs);
    }

    return eventRepository.save(event);
  }

  @Override
  public List<Event> findAll() {
    return eventRepository.findAll();
  }

  private void removeEventFromCostsAndUsers(Event event) {
    event
        .getParticipants()
        .forEach(
            user -> {
              user.getEvents().remove(event);
              userRepository.save(user);
            });
    event.getParticipants().clear();
  }

  @Override
  @Transactional
  public Event delete(Event event) {
    if (event == null) {
      throw new IllegalArgumentException("Cannot delete a null event.");
    }
    removeEventFromCostsAndUsers(event);
    eventRepository.delete(event);
    return event;
  }

  @Override
  @Transactional
  public Event deleteById(Long id) {
    Event event =
        eventRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + id));
    removeEventFromCostsAndUsers(event);
    eventRepository.delete(event);

    return event;
  }
}
