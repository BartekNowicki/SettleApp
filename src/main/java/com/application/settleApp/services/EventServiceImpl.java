package com.application.settleApp.services;

import com.application.settleApp.models.Event;
import com.application.settleApp.repositories.EventRepository;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EventServiceImpl implements EventService {

  private final EventRepository eventRepository;

  @Override
  public Event findById(Long id) {
    return eventRepository.findById(id).orElse(null);
  }

  @Override
  public Event save(Event object) {
    return eventRepository.save(object);
  }

  @Override
  public Set<Event> findAll() {
    return new HashSet<>(eventRepository.findAll());
  }

  @Override
  public Event delete(Event object) {
    eventRepository.delete(object);
    return object;
  }

  @Override
  public Long deleteById(Long id) {
    eventRepository.deleteById(id);
    return id;
  }
}
