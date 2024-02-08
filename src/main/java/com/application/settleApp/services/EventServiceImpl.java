package com.application.settleApp.services;

import com.application.settleApp.models.Cost;
import com.application.settleApp.models.Event;
import com.application.settleApp.models.User;
import com.application.settleApp.repositories.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
public class EventServiceImpl implements EventService {

  private final EventRepository eventRepository;
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

  public Event save(Event event, Set<Long> relatedParticipantIds, Set<Long> relatedProductIds) {

    if (relatedParticipantIds != null && !relatedParticipantIds.isEmpty()) {
      Set<User> participants =
          relatedParticipantIds.stream()
              .map(participantId -> userService.findById(participantId))
              .collect(Collectors.toSet());
      event.setParticipants(participants);
    }

    if (relatedProductIds != null && !relatedProductIds.isEmpty()) {
      Set<Cost> costs =
          relatedProductIds.stream()
              .map(productId -> costService.findById(productId))
              .collect(Collectors.toSet());
      event.setCosts(costs);
    }

    return eventRepository.save(event);
  }

  @Override
  public List<Event> findAll() {
    return eventRepository.findAll();
  }

  @Override
  public Event delete(Event event) {
    if (event == null) {
      throw new IllegalArgumentException("Cannot delete a null event.");
    }
    eventRepository.delete(event);
    return event;
  }

  @Override
  public Event deleteById(Long id) {
    Event event =
        eventRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + id));
    eventRepository.delete(event);

    return event;
  }
}
