package com.application.settleApp.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.application.settleApp.DTOs.EventDTO;
import com.application.settleApp.enums.Status;
import com.application.settleApp.mappers.EventMapper;
import com.application.settleApp.model.Event;
import com.application.settleApp.model.User;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
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
    eventRepository.deleteAll();
    userRepository.deleteAll();

    testEvent1 = new Event();
    testEvent1.setEventDate(LocalDate.of(2024, 1, 4));
    testEvent1.setStatus(Status.OPEN);
    testEvent1 = eventRepository.save(testEvent1);

    testEvent2 = new Event();
    testEvent2.setEventDate(LocalDate.of(2024, 2, 4));
    testEvent2.setStatus(Status.OPEN);
    testEvent2 = eventRepository.save(testEvent2);
  }

  @Test
  @Transactional
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
  public void createEvent_WithId_ThrowsIllegalArgumentException() throws Exception {
    EventDTO eventDTO = new EventDTO();
    eventDTO.setEventId(1L);

    mockMvc.perform(post("/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(eventDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Id is autoincremented and should not be provided")));
  }

  @Test
  @Transactional
  public void getAllEvents_Success() throws Exception {
    mockMvc.perform(get("/events")).andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
  }

  @Test
  @Transactional
  public void updateEventWithParticipantAndVerifyAssociation() throws Exception {
    User newUser = new User();
    newUser = userRepository.save(newUser);

    Event newEvent = new Event();
    newEvent.setStatus(Status.OPEN);
    newEvent = eventRepository.save(newEvent);

    EventDTO eventDTO = new EventMapper().toDTO(newEvent);
    eventDTO.setStatus(Status.CLOSED);
    eventDTO.setParticipantIds(Set.of(newUser.getUserId()));

    mockMvc
        .perform(
            patch("/events/" + newEvent.getEventId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(Status.CLOSED.toString()));

    Event updatedEvent = eventRepository.findById(newEvent.getEventId()).orElseThrow();
    User updatedUser = userRepository.findById(newUser.getUserId()).orElseThrow();

    assertTrue(
        updatedEvent.getParticipants().contains(updatedUser),
        "Event does not contain the updated participant");
  }

  @Test
  @Transactional
  public void deleteEventTest_Success() throws Exception {
    Event newEvent = new Event();
    newEvent.setStatus(Status.OPEN);
    newEvent = eventRepository.save(newEvent);

    mockMvc.perform(delete("/events/" + newEvent.getEventId())).andExpect(status().isOk());

    mockMvc.perform(get("/events/" + newEvent.getEventId())).andExpect(status().isNotFound());
  }
}
