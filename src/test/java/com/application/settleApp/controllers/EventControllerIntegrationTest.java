package com.application.settleApp.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.application.settleApp.DTOs.EventDTO;
import com.application.settleApp.enums.ExceptionMessage;
import com.application.settleApp.enums.StatusType;
import com.application.settleApp.mappers.EventMapper;
import com.application.settleApp.models.BaseEntity;
import com.application.settleApp.models.Event;
import com.application.settleApp.models.Role;
import com.application.settleApp.models.User;
import com.application.settleApp.repositories.EventRepository;
import com.application.settleApp.repositories.UserRepository;
import com.application.settleApp.security.AuthRequest;
import com.application.settleApp.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
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
  @Autowired private AuthService authService;
  @Autowired private JdbcTemplate jdbcTemplate;

  private Event testEvent1;
  private Event testEvent2;
  private String userToken;

  @BeforeEach
  public void setup() {
    eventRepository.deleteAll();
    userRepository.deleteAll();

    //this is needed because of foreign key costraints
    String insertRoleSql =
            "INSERT INTO role (role_id, name)\n"
                    + "SELECT 1, 'USER'\n"
                    + "WHERE NOT EXISTS (\n"
                    + "  SELECT 1 FROM role WHERE role_id = 1\n"
                    + ");";
    jdbcTemplate.update(insertRoleSql);

    testEvent1 = BaseEntity.getNewWithDefaultDates(Event.class);
    testEvent1.setEventDate(LocalDate.of(2024, 1, 4));
    testEvent1.setStatus(StatusType.OPEN);
    testEvent1 = eventRepository.save(testEvent1);

    testEvent2 = BaseEntity.getNewWithDefaultDates(Event.class);
    testEvent2.setEventDate(LocalDate.of(2024, 2, 4));
    testEvent2.setStatus(StatusType.OPEN);
    testEvent2 = eventRepository.save(testEvent2);

    User userMakingRequests = BaseEntity.getNewWithDefaultDates(User.class);
    userMakingRequests.setEmail("userMakingRequests@example.com");
    String passwordNotHashed = "hashed_password1";
    String passwordHashedStoredInDb =
        "$2b$12$iU/7c.jaS5Ze57mBdxXMUuGrkhjOzeZ3ZZVF6mA6nZMAdUv57jnuK";
    userMakingRequests.setPassword(passwordHashedStoredInDb);
    Role userMakingRequestsRole = new Role();
    userMakingRequestsRole.setRoleId(1L);
    userMakingRequestsRole.setName("USER");
    userMakingRequests.getRoles().add(userMakingRequestsRole);
    userRepository.save(userMakingRequests);
    userToken =
        "Bearer "
            + authService.generateToken(
                new AuthRequest(userMakingRequests.getEmail(), passwordNotHashed));
  }

  private HttpHeaders getAuthorizationHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", userToken);
    return headers;
  }

  @Test
  @Transactional
  public void createEventTest_Success() throws Exception {
    EventDTO eventDTO = new EventDTO();
    eventDTO.setStatusType(StatusType.OPEN);

    mockMvc
        .perform(
            post("/events")
                .headers(getAuthorizationHeaders())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDTO)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.statusType").value(StatusType.OPEN.toString()));
  }

  @Test
  public void createEvent_WithId_ThrowsIllegalArgumentException() throws Exception {
    EventDTO eventDTO = new EventDTO();
    eventDTO.setEventId(1L);

    mockMvc
        .perform(
            post("/events")
                .headers(getAuthorizationHeaders())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDTO)))
        .andExpect(status().isBadRequest())
        .andExpect(
            content().string(containsString(ExceptionMessage.ID_AUTOINCREMENTED.getMessage())));
  }

  @Test
  @Transactional
  public void getAllEvents_Success() throws Exception {
    mockMvc
        .perform(get("/events").headers(getAuthorizationHeaders()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  @Test
  @Transactional
  public void updateEventWithParticipantAndVerifyAssociation() throws Exception {
    User newUser = BaseEntity.getNewWithDefaultDates(User.class);
    newUser = userRepository.save(newUser);

    Event newEvent = BaseEntity.getNewWithDefaultDates(Event.class);
    newEvent.setStatus(StatusType.OPEN);
    newEvent = eventRepository.save(newEvent);

    EventDTO eventDTO = new EventMapper().toDTO(newEvent);
    eventDTO.setStatusType(StatusType.CLOSED);
    eventDTO.setParticipantIds(Set.of(newUser.getId()));

    mockMvc
        .perform(
            patch("/events/" + newEvent.getId())
                .headers(getAuthorizationHeaders())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.statusType").value(StatusType.CLOSED.toString()));

    Event updatedEvent = eventRepository.findById(newEvent.getId()).orElseThrow();
    User updatedUser = userRepository.findById(newUser.getId()).orElseThrow();

    assertTrue(
        updatedEvent.getParticipants().contains(updatedUser),
        "Event does not contain the updated participant");
  }

  @Test
  @Transactional
  public void deleteEventTest_Success() throws Exception {
    Event newEvent = BaseEntity.getNewWithDefaultDates(Event.class);
    newEvent.setStatus(StatusType.OPEN);
    newEvent = eventRepository.save(newEvent);

    mockMvc
        .perform(delete("/events/" + newEvent.getId()).headers(getAuthorizationHeaders()))
        .andExpect(status().isOk());

    mockMvc
        .perform(get("/events/" + newEvent.getId()).headers(getAuthorizationHeaders()))
        .andExpect(status().isNotFound());
  }
}
