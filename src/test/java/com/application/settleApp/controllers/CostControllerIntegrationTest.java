package com.application.settleApp.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.application.settleApp.DTOs.CostDTO;
import com.application.settleApp.DTOs.UserDTO;
import com.application.settleApp.models.Cost;
import com.application.settleApp.models.Event;
import com.application.settleApp.models.User;
import com.application.settleApp.repositories.CostRepository;
import com.application.settleApp.repositories.EventRepository;
import com.application.settleApp.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class CostControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private UserRepository userRepository;
  @Autowired private EventRepository eventRepository;
  @Autowired private CostRepository costRepository;
  @Autowired private ObjectMapper objectMapper;

  private User testUser1;
  private User testUser2;
  private Event testEvent;

  @BeforeEach
  public void setup() {
    testUser1 = new User();
    testUser1.setUserId(1L);
    testUser1 = userRepository.save(testUser1);

    testUser2 = new User();
    testUser2.setUserId(2L);
    testUser2 = userRepository.save(testUser2);

    testEvent = new Event();
    testEvent.setEventId(1L);
    testEvent = eventRepository.save(testEvent);
  }

  @Test
  public void createCost_Success() throws Exception {
    String costJson =
        String.format(
            "{\"name\":\"%s\", \"productId\":%d, \"userId\":%d, \"eventId\":%d}",
            "biggie", 1, 1, 1);
    mockMvc
        .perform(post("/costs").contentType(MediaType.APPLICATION_JSON).content(costJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("biggie"));
  }

  @Test
  public void createCost_MissingUserIdOrEventId_ThrowsException() throws Exception {
    String updatedCostJson = "{\"productId\":" + 1 + ", \"userId\":2}";
    mockMvc
        .perform(post("/costs").contentType(MediaType.APPLICATION_JSON).content(updatedCostJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Both userId and eventId must be provided."));
  }

  @Test
  public void patchCost_ReassignToDifferentUser() throws Exception {
    String updatedCostJson =
        String.format("{\"productId\":%d, \"userId\":%d, \"eventId\":%d}", 1, 2, 1);

    mockMvc
        .perform(patch("/costs/1").contentType(MediaType.APPLICATION_JSON).content(updatedCostJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(2));
  }

  @Test
  public void deleteCost_ThatDoesNotExist_ThrowsNotFound() throws Exception {
    long nonExistentCostId = 99999L;

    mockMvc
        .perform(delete("/costs/" + nonExistentCostId).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Cost not found with id: " + nonExistentCostId));
  }

  @Test
  public void deleteCostAndVerifyItIsRemoved() throws Exception {
    User testUser = new User();
    testUser = userRepository.save(testUser);
    Event testEvent = new Event();
    testEvent = eventRepository.save(testEvent);
    Cost testCost = new Cost();
    testCost.setUser(testUser);
    testCost.setEvent(testEvent);
    testCost = costRepository.save(testCost);

    mockMvc
        .perform(
            delete("/costs/" + testCost.getProductId()).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    assertFalse(
        costRepository.existsById(testCost.getProductId()),
        "Cost should no longer exist in the database");
  }

  @Test
  @Transactional
  public void verifyDeletedCostRemovedAndAssociationsGoneButRelatedEntitiesRemain()
      throws Exception {
    User testUser = new User();
    testUser = userRepository.save(testUser);
    Event testEvent = new Event();
    testEvent = eventRepository.save(testEvent);
    Cost testCost = new Cost();
    testCost.setUser(testUser);
    testCost.setEvent(testEvent);
    testCost = costRepository.save(testCost);

    long userId = testUser.getUserId();
    long eventId = testEvent.getEventId();

    mockMvc
        .perform(
            delete("/costs/" + testCost.getProductId()).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    assertFalse(
        costRepository.existsById(testCost.getProductId()),
        "Cost should no longer exist in the database");

    assertTrue(userRepository.existsById(userId), "User should still exist in the database");
    assertTrue(eventRepository.existsById(eventId), "Event should still exist in the database");

    User remainingUser =
        userRepository.findById(userId).orElseThrow(() -> new AssertionError("User not found"));
    Event remainingEvent =
        eventRepository.findById(eventId).orElseThrow(() -> new AssertionError("Event not found"));

    assertTrue(
        remainingUser.getCosts().isEmpty(), "User should no longer have any associated Costs");
    assertTrue(
        remainingEvent.getCosts().isEmpty(), "Event should no longer have any associated Costs");
  }

  @Test
  @Transactional
  public void updateCostWithUserAndVerifyAssociation() throws Exception {
    UserDTO newUser = new UserDTO();
    String newUserJson = objectMapper.writeValueAsString(newUser);
    String userResponse =
        mockMvc
            .perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(newUserJson))
            .andReturn()
            .getResponse()
            .getContentAsString();
    UserDTO createdUser = objectMapper.readValue(userResponse, UserDTO.class);

    CostDTO newCost = new CostDTO();
    newCost.setUserId(createdUser.getUserId());
    newCost.setEventId(1L);
    String newCostJson = objectMapper.writeValueAsString(newCost);
    String costResponse =
        mockMvc
            .perform(post("/costs").contentType(MediaType.APPLICATION_JSON).content(newCostJson))
            .andReturn()
            .getResponse()
            .getContentAsString();
    CostDTO createdCost = objectMapper.readValue(costResponse, CostDTO.class);

    Cost updatedCost =
        costRepository
            .findById(createdCost.getProductId())
            .orElseThrow(() -> new AssertionError("Cost not found"));
    User associatedUser =
        userRepository
            .findById(createdUser.getUserId())
            .orElseThrow(() -> new AssertionError("User not found"));

    assertEquals(
        associatedUser.getUserId(),
        updatedCost.getUser().getUserId(),
        "Cost does not contain the correct user");

    boolean containsCost =
        associatedUser.getCosts().stream()
            .anyMatch(cost -> cost.getProductId() == updatedCost.getProductId());

    assertTrue(containsCost, "User does not have the expected cost in their collection of costs");
  }
}
