package com.application.settleApp.mappers;

import com.application.settleApp.DTOs.EventDTO;
import com.application.settleApp.models.Cost;
import com.application.settleApp.models.Event;
import com.application.settleApp.models.User;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

  @Transactional
  public EventDTO toDTO(Event event) {
    EventDTO dto = new EventDTO();
    dto.setEventId(event.getEventId());
    dto.setStatus(event.getStatus());
    dto.setEventDate(event.getEventDate());
    dto.setCreatedByUserId(event.getCreatedByUserId());
    dto.setParticipantIds(
        event.getParticipants().stream().map(User::getUserId).collect(Collectors.toSet()));
    dto.setProductIds(
        event.getCosts().stream().map(Cost::getProductId).collect(Collectors.toSet()));

    return dto;
  }

  public static Event fromDTO(EventDTO dto) {
    Event event = new Event();
    event.setEventId(dto.getEventId());
    event.setStatus(dto.getStatus());
    event.setEventDate(dto.getEventDate());
    event.setCreatedByUserId(dto.getCreatedByUserId());

    return event;
  }
}
