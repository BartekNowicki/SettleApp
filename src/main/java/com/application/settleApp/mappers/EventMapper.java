package com.application.settleApp.mappers;

import com.application.settleApp.DTOs.EventDTO;
import com.application.settleApp.models.BaseEntity;
import com.application.settleApp.models.Cost;
import com.application.settleApp.models.Event;
import com.application.settleApp.models.User;
import jakarta.transaction.Transactional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class EventMapper implements Mapper<Event, EventDTO> {

  @Transactional
  public EventDTO toDTO(Event event) {
    EventDTO dto = new EventDTO();
    dto.setEventId(event.getId());
    dto.setStatusType(event.getStatus());
    dto.setEventDate(event.getEventDate());
    dto.setCreatedByUserId(event.getCreatedByUserId());
    dto.setParticipantIds(
        event.getParticipants().stream().map(User::getId).collect(Collectors.toSet()));
    dto.setProductIds(
        event.getCosts().stream().map(Cost::getId).collect(Collectors.toSet()));

    return dto;
  }

  public Event fromDTO(EventDTO dto) {
    Event event = BaseEntity.getNewWithDefaultDates(Event.class);
    event.setId(dto.getEventId());
    event.setStatus(dto.getStatusType());
    event.setEventDate(dto.getEventDate());
    event.setCreatedByUserId(dto.getCreatedByUserId());

    return event;
  }
}
