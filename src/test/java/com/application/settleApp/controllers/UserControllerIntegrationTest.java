package com.application.settleApp.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.application.settleApp.DTOs.UserDTO;
import com.application.settleApp.models.User;
import com.application.settleApp.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private UserRepository userRepository;

  private User testUser1;
  private User testUser2;

  private UserDTO newUser;

  @BeforeEach
  public void setup() {
    testUser1 = new User();
    testUser1.setFname("testUser1_fname");
    testUser1 = userRepository.save(testUser1);

    testUser2 = new User();
    testUser2.setFname("testUser2_fname");
    testUser2 = userRepository.save(testUser2);

    newUser = new UserDTO();
    newUser.setFname("Test");
    newUser.setLname("User");
  }

  @Test
  public void createUser_Success() throws Exception {
    mockMvc
        .perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.fname").value(newUser.getFname()));
  }

  @Test
  public void getUserById_Success() throws Exception {
    String responseBody =
        mockMvc
            .perform(
                post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newUser)))
            .andReturn()
            .getResponse()
            .getContentAsString();

    UserDTO createdUser = objectMapper.readValue(responseBody, UserDTO.class);

    mockMvc
        .perform(get("/users/" + createdUser.getUserId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(createdUser.getUserId()));
  }

  @Test
  public void getAllUsers_Success() throws Exception {
    mockMvc
        .perform(get("/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  public void updateUser_Success() throws Exception {
    String responseBody =
        mockMvc
            .perform(
                post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newUser)))
            .andReturn()
            .getResponse()
            .getContentAsString();

    UserDTO createdUser = objectMapper.readValue(responseBody, UserDTO.class);

    createdUser.setFname("Updated Name");

    mockMvc
        .perform(
            patch("/users/" + createdUser.getUserId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createdUser)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.fname").value("Updated Name"));
  }

  @Test
  public void deleteUser_Success() throws Exception {
    String responseBody =
        mockMvc
            .perform(
                post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newUser)))
            .andReturn()
            .getResponse()
            .getContentAsString();

    UserDTO createdUser = objectMapper.readValue(responseBody, UserDTO.class);

    mockMvc.perform(delete("/users/" + createdUser.getUserId())).andExpect(status().isOk());

    mockMvc.perform(get("/users/" + createdUser.getUserId())).andExpect(status().isNotFound());
  }

  @Test
  public void updateUser_MismatchUserId_ThrowsException() throws Exception {
    long pathUserId = 1L;
    UserDTO userDTO = new UserDTO();
    userDTO.setUserId(2L);

    String userJson = objectMapper.writeValueAsString(userDTO);

    mockMvc
        .perform(
            patch("/users/" + pathUserId).contentType(MediaType.APPLICATION_JSON).content(userJson))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.message").value("Mismatch between path variable userId and userDTO id"));
  }

  @Test
  public void deleteUser_ThatDoesNotExist_ThrowsNotFound() throws Exception {
    long nonExistentUserId = 99999L;

    mockMvc
        .perform(delete("/users/" + nonExistentUserId).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("User not found with id: " + nonExistentUserId));
  }
}
