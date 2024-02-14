package com.application.settleApp.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.application.settleApp.DTOs.CostDTO;
import com.application.settleApp.mappers.CostMapper;
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
    testUser1 = userRepository.save(new User());
    testUser2 = userRepository.save(new User());
    testEvent = eventRepository.save(new Event());
  }

  @Test
  @Transactional
  public void createCost_Success() throws Exception {
    CostDTO costDTO = new CostDTO();
    costDTO.setName("Test Cost");
    costDTO.setUserId(testUser1.getUserId());
    costDTO.setEventId(testEvent.getEventId());

    mockMvc
        .perform(
            post("/costs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(costDTO)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Test Cost"));
  }

  @Test
  @Transactional
  public void createCost_MissingUserIdOrEventId_ThrowsException() throws Exception {
    CostDTO costDTO = new CostDTO();

    mockMvc
        .perform(
            post("/costs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(costDTO)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @Transactional
  public void patchCost_ReassignToDifferentUser() throws Exception {
    Cost savedCost = costRepository.save(new Cost());

    CostDTO updatedCostDTO = new CostMapper().toDTO(savedCost);
    updatedCostDTO.setUserId(testUser2.getUserId());
    updatedCostDTO.setEventId(testEvent.getEventId());

    mockMvc
        .perform(
            patch("/costs/" + savedCost.getProductId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCostDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(testUser2.getUserId()));
  }

  @Test
  @Transactional
  public void deleteCost_ThatDoesNotExist_ThrowsNotFound() throws Exception {
    long nonExistentCostId = -1L;

    mockMvc.perform(delete("/costs/" + nonExistentCostId)).andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  public void deleteCostAndVerifyItIsRemoved() throws Exception {
    Cost savedCost = costRepository.save(new Cost());

    mockMvc.perform(delete("/costs/" + savedCost.getProductId())).andExpect(status().isOk());

    assertFalse(costRepository.existsById(savedCost.getProductId()));
  }

  @Test
  @Transactional
  public void updateCostWithUserAndVerifyAssociation() throws Exception {
    Cost newCost = new Cost();
    newCost.setUser(testUser2);
    Cost savedCost = costRepository.save(newCost);

    CostDTO costDTO = new CostMapper().toDTO(savedCost);
    costDTO.setEventId(testEvent.getEventId());

    mockMvc
        .perform(
            patch("/costs/" + savedCost.getProductId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(costDTO)))
        .andExpect(status().isOk());

    assertTrue(costRepository.findById(savedCost.getProductId()).isPresent());
    Cost updatedCost = costRepository.findById(savedCost.getProductId()).get();
    assertEquals(testUser2.getUserId(), updatedCost.getUser().getUserId());
  }
}
