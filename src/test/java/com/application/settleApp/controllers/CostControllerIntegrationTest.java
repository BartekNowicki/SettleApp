package com.application.settleApp.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.application.settleApp.DTOs.CostDTO;
import com.application.settleApp.mappers.CostMapper;
import com.application.settleApp.models.BaseEntity;
import com.application.settleApp.models.Cost;
import com.application.settleApp.models.Event;
import com.application.settleApp.models.Role;
import com.application.settleApp.models.User;
import com.application.settleApp.repositories.CostRepository;
import com.application.settleApp.repositories.EventRepository;
import com.application.settleApp.repositories.UserRepository;
import com.application.settleApp.security.AuthRequest;
import com.application.settleApp.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
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
public class CostControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private UserRepository userRepository;
  @Autowired private EventRepository eventRepository;
  @Autowired private CostRepository costRepository;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private AuthService authService;
  @Autowired private JdbcTemplate jdbcTemplate;

  private User testUser1;
  private User testUser2;
  private Event testEvent;
  private String userToken;

  @BeforeEach
  public void setup() {
    // this is needed because of foreign key costraints
    String insertRoleSql =
        "INSERT INTO `role` (role_id, name)\n" // <-- Here
            + "SELECT 1, 'USER'\n"
            + "WHERE NOT EXISTS (\n"
            + "  SELECT 1 FROM `role` WHERE role_id = 1\n" // <-- Here
            + ");";
    jdbcTemplate.update(insertRoleSql);
    userRepository.deleteAll();

    testUser1 = userRepository.save(BaseEntity.getNewWithDefaultDates(User.class));
    testUser2 = userRepository.save(BaseEntity.getNewWithDefaultDates(User.class));
    testEvent = eventRepository.save(BaseEntity.getNewWithDefaultDates(Event.class));

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
  public void createCost_Success() throws Exception {
    CostDTO costDTO = new CostDTO();
    costDTO.setName("Test Cost");
    costDTO.setUserId(testUser1.getId());
    costDTO.setEventId(testEvent.getId());

    mockMvc
        .perform(
            post("/costs")
                .headers(getAuthorizationHeaders())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(costDTO)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Test Cost"));
  }

  @Test
  public void createCost_WithId_ThrowsIllegalArgumentException() throws Exception {
    CostDTO costDTO = new CostDTO();
    costDTO.setProductId(1L);

    mockMvc
        .perform(
            post("/costs")
                .headers(getAuthorizationHeaders())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(costDTO)))
        .andExpect(status().isBadRequest())
        .andExpect(
            content().string(containsString("Id is autoincremented and should not be provided")));
  }

  @Test
  @Transactional
  public void createCost_MissingUserIdOrEventId_ThrowsException() throws Exception {
    CostDTO costDTO = new CostDTO();

    mockMvc
        .perform(
            post("/costs")
                .headers(getAuthorizationHeaders())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(costDTO)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @Transactional
  public void patchCost_ReassignToDifferentUser() throws Exception {
    Cost savedCost = costRepository.save(BaseEntity.getNewWithDefaultDates(Cost.class));

    CostDTO updatedCostDTO = new CostMapper().toDTO(savedCost);
    updatedCostDTO.setUserId(testUser2.getId());
    updatedCostDTO.setEventId(testEvent.getId());

    mockMvc
        .perform(
            patch("/costs/" + savedCost.getId())
                .headers(getAuthorizationHeaders())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCostDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(testUser2.getId()));
  }

  @Test
  @Transactional
  public void deleteCost_ThatDoesNotExist_ThrowsNotFound() throws Exception {
    long nonExistentCostId = -1L;

    mockMvc
        .perform(delete("/costs/" + nonExistentCostId).headers(getAuthorizationHeaders()))
        .andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  public void deleteCostAndVerifyItIsRemoved() throws Exception {
    Cost savedCost = costRepository.save(BaseEntity.getNewWithDefaultDates(Cost.class));

    mockMvc
        .perform(delete("/costs/" + savedCost.getId()).headers(getAuthorizationHeaders()))
        .andExpect(status().isOk());

    assertFalse(costRepository.existsById(savedCost.getId()));
  }

  @Test
  @Transactional
  public void updateCostWithUserAndVerifyAssociation() throws Exception {
    Cost newCost = BaseEntity.getNewWithDefaultDates(Cost.class);
    newCost.setUser(testUser2);
    Cost savedCost = costRepository.save(newCost);

    CostDTO costDTO = new CostMapper().toDTO(savedCost);
    costDTO.setEventId(testEvent.getId());

    mockMvc
        .perform(
            patch("/costs/" + savedCost.getId())
                .headers(getAuthorizationHeaders())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(costDTO)))
        .andExpect(status().isOk());

    assertTrue(costRepository.findById(savedCost.getId()).isPresent());
    Cost updatedCost = costRepository.findById(savedCost.getId()).get();
    assertEquals(testUser2.getId(), updatedCost.getUser().getId());
  }

  @Test
  @Transactional
  public void patchCost_ReassignToDifferentUser_AndVerifyAssociations() throws Exception {
    User initialUser = BaseEntity.getNewWithDefaultDates(User.class);
    initialUser.setFname("Initial User");
    initialUser = userRepository.save(initialUser);

    Event initialEvent = BaseEntity.getNewWithDefaultDates(Event.class);
    initialEvent = eventRepository.save(initialEvent);

    Cost initialCost = BaseEntity.getNewWithDefaultDates(Cost.class);
    initialCost.setUser(initialUser);
    initialCost.setEvent(initialEvent);
    initialCost = costRepository.save(initialCost);

    CostDTO costDTO = new CostMapper().toDTO(initialCost);

    User newUser = BaseEntity.getNewWithDefaultDates(User.class);
    newUser.setFname("New User");
    newUser = userRepository.save(newUser);

    costDTO.setUserId(newUser.getId());

    String updatedCostJson = objectMapper.writeValueAsString(costDTO);

    mockMvc
        .perform(
            patch("/costs/" + initialCost.getId())
                .headers(getAuthorizationHeaders())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedCostJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(newUser.getId()));

    User updatedUser = userRepository.findById(newUser.getId()).orElseThrow();
    Event updatedEvent = eventRepository.findById(initialEvent.getId()).orElseThrow();
    Cost updatedCost = costRepository.findById(initialCost.getId()).orElseThrow();

    assertEquals(
        updatedUser.getId(),
        updatedCost.getUser().getId(),
        "Cost is not reassigned to the new user");
    assertTrue(
        updatedEvent.getCosts().contains(updatedCost), "Event does not contain the updated cost");
    assertTrue(
        updatedUser.getCosts().contains(updatedCost),
        "New user does not contain the reassigned cost");
  }
}
