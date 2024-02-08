package com.application.settleApp.controllers;

import com.application.settleApp.DTOs.EventDTO;
import com.application.settleApp.mappers.EventMapper;
import com.application.settleApp.models.Event;
import com.application.settleApp.services.EventServiceImpl;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/events")
public class EventController {

  private final EventServiceImpl eventService;
  private final EventMapper eventMapper;

  @PostMapping
  public ResponseEntity<EventDTO> createEvent(@RequestBody EventDTO eventDTO) {
    Event createdEvent = eventService.save(eventMapper.fromDTO(eventDTO));
    return new ResponseEntity<>(eventMapper.toDTO(createdEvent), HttpStatus.CREATED);
  }

  @GetMapping("/{eventId}")
  public ResponseEntity<EventDTO> getEventById(@PathVariable Long eventId) {
    Event event = eventService.findById(eventId);
    return ResponseEntity.ok(eventMapper.toDTO(event));
  }

  @GetMapping
  public ResponseEntity<List<EventDTO>> getAllEvents() {
    List<Event> events = eventService.findAll();
    List<EventDTO> eventDTOs = events.stream().map(eventMapper::toDTO).collect(Collectors.toList());
    return ResponseEntity.ok(eventDTOs);
  }

  @PatchMapping("/{eventId}")
  public ResponseEntity<?> updateEvent(@PathVariable Long eventId, @RequestBody EventDTO eventDTO) {
    if (eventDTO.getEventId() != null && !Objects.equals(eventId, eventDTO.getEventId())) {
      return ResponseEntity.badRequest()
          .body("Mismatch between path variable eventId and eventDTO id");
    }

    Event updatedEvent;
    if ((eventDTO.getParticipantIds() != null && !eventDTO.getParticipantIds().isEmpty())
        || (eventDTO.getProductIds() != null && !eventDTO.getProductIds().isEmpty())) {
      updatedEvent =
          eventService.save(
              eventMapper.fromDTO(eventDTO),
              eventDTO.getParticipantIds(),
              eventDTO.getProductIds());
    } else {
      updatedEvent = eventService.save(eventMapper.fromDTO(eventDTO));
    }
    return ResponseEntity.ok(eventMapper.toDTO(updatedEvent));
  }

  @DeleteMapping("/{eventId}")
  public ResponseEntity<EventDTO> deleteEvent(@PathVariable Long eventId) {
    Event deletedEvent = eventService.deleteById(eventId);
    EventDTO deletedEventDTO = eventMapper.toDTO(deletedEvent);
    return ResponseEntity.ok(deletedEventDTO);
  }
}
