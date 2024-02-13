package com.application.settleApp.controllers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.application.settleApp.DTOs.EventDTO;
import com.application.settleApp.DTOs.UserDTO;
import com.application.settleApp.enums.Status;
import com.application.settleApp.models.Event;
import com.application.settleApp.models.User;
import com.application.settleApp.repositories.CostRepository;
import com.application.settleApp.repositories.EventRepository;
import com.application.settleApp.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private UserRepository userRepository;
  @Autowired private EventRepository eventRepository;

  @Autowired private ObjectMapper objectMapper;

  private Event testEvent1;
  private Event testEvent2;

  @BeforeEach
  public void setup() {
    testEvent1 = new Event();
    testEvent1.setEventId(1L);
    LocalDate testEvent1Date = LocalDate.of(2024, 1, 4);
    testEvent1.setEventDate(testEvent1Date);
    testEvent1 = eventRepository.save(testEvent1);

    testEvent2 = new Event();
    testEvent2.setEventId(2L);
    LocalDate testEvent2Date = LocalDate.of(2024, 2, 4);
    testEvent1.setEventDate(testEvent2Date);
    testEvent1 = eventRepository.save(testEvent2);
  }

  @Test
  public void createEventTest_Success() throws Exception {
    EventDTO eventDTO = new EventDTO();
    eventDTO.setStatus(Status.OPEN);

    mockMvc
        .perform(
            post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDTO)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value(Status.OPEN.toString()));
  }

  @Test
  public void getEventById_Success() throws Exception {

    mockMvc
        .perform(get("/events/" + testEvent1.getEventId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.eventDate").value(testEvent1.getEventDate()));
  }

  @Test
  public void getAllEvents_Success() throws Exception {
    mockMvc
        .perform(get("/events"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  public void updateEvent_Success() throws Exception {
    EventDTO eventDTO = new EventDTO();

    String eventResponse =
        mockMvc
            .perform(
                post("/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(eventDTO)))
            .andReturn()
            .getResponse()
            .getContentAsString();

    EventDTO createdEvent = objectMapper.readValue(eventResponse, EventDTO.class);

    createdEvent.setStatus(Status.CLOSED);

    mockMvc
        .perform(
            patch("/events/" + createdEvent.getEventId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createdEvent)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(Status.CLOSED.toString()));
  }

  @Test
  public void updateEvent_MismatchEventId_ThrowsException() throws Exception {
    EventDTO eventDTO = new EventDTO();
    eventDTO.setEventId(2L);

    String eventJson = objectMapper.writeValueAsString(eventDTO);

    mockMvc
        .perform(patch("/events/1").contentType(MediaType.APPLICATION_JSON).content(eventJson))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.message").value("Mismatch between path variable eventId and eventDTO id"));
  }

  @Test
  @Transactional
  public void deleteEventTest_Success() throws Exception {
    EventDTO eventDTO = new EventDTO();

    String eventResponse =
        mockMvc
            .perform(
                post("/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(eventDTO)))
            .andReturn()
            .getResponse()
            .getContentAsString();

    EventDTO createdEvent = objectMapper.readValue(eventResponse, EventDTO.class);

    mockMvc.perform(delete("/events/" + createdEvent.getEventId())).andExpect(status().isOk());

    mockMvc.perform(get("/events/" + createdEvent.getEventId())).andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  public void updateEventWithParticipantAndVerifyAssociation() throws Exception {
    UserDTO newUser = new UserDTO();
    String newUserJson = objectMapper.writeValueAsString(newUser);
    String userResponse =
        mockMvc
            .perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(newUserJson))
            .andReturn()
            .getResponse()
            .getContentAsString();
    UserDTO createdUser = objectMapper.readValue(userResponse, UserDTO.class);

    EventDTO newEvent = new EventDTO();
    String newEventJson = objectMapper.writeValueAsString(newEvent);
    String eventResponse =
        mockMvc
            .perform(post("/events").contentType(MediaType.APPLICATION_JSON).content(newEventJson))
            .andReturn()
            .getResponse()
            .getContentAsString();
    EventDTO createdEvent = objectMapper.readValue(eventResponse, EventDTO.class);

    createdEvent.setParticipantIds(Set.of(createdUser.getUserId()));
    String updatedEventJson = objectMapper.writeValueAsString(createdEvent);
    mockMvc
        .perform(
            patch("/events/" + createdEvent.getEventId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedEventJson))
        .andExpect(status().isOk());

    Event updatedEvent =
        eventRepository
            .findById(createdEvent.getEventId())
            .orElseThrow(() -> new AssertionError("Event not found"));
    User participant =
        userRepository
            .findById(createdUser.getUserId())
            .orElseThrow(() -> new AssertionError("User not found"));

    assertTrue(
        updatedEvent.getParticipants().contains(participant),
        "Updated event does not contain the user as a participant");

    assertTrue(
        participant.getEvents().contains(updatedEvent),
        "Updated event does not contain the user as a participant");
  }
}
