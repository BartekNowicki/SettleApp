package com.application.settleApp.controllers;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.application.settleApp.DTOs.UserDTO;
import com.application.settleApp.models.User;
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
public class UserControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private UserRepository userRepository;

  @BeforeEach
  public void setup() {
    userRepository.deleteAll();
  }

  @Test
  @Transactional
  public void createUser_Success() throws Exception {
    UserDTO newUser = new UserDTO();
    newUser.setFname("New");
    newUser.setLname("User");

    mockMvc
        .perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.fname").value(newUser.getFname()));
  }

  @Test
  @Transactional
  public void getAllUsers_Success() throws Exception {
    mockMvc.perform(get("/users")).andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
  }

  @Test
  @Transactional
  public void updateUser_Success() throws Exception {
    User user = new User();
    user.setFname("Initial Name");
    User savedUser = userRepository.save(user);

    UserDTO userDTO = new UserDTO();
    userDTO.setUserId(savedUser.getUserId());
    userDTO.setFname("Updated Name");

    mockMvc
        .perform(
            patch("/users/" + savedUser.getUserId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.fname").value("Updated Name"));
  }

  @Test
  @Transactional
  public void deleteUser_Success() throws Exception {
    User user = new User();
    user.setFname("To Be Deleted");
    User savedUser = userRepository.save(user);

    mockMvc.perform(delete("/users/" + savedUser.getUserId())).andExpect(status().isOk());

    mockMvc.perform(get("/users/" + savedUser.getUserId())).andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  public void updateUser_MismatchUserId_ThrowsException() throws Exception {
    User user = userRepository.save(new User());
    long pathUserId = user.getUserId() + 1; // Assuming the next ID would be a mismatch

    UserDTO userDTO = new UserDTO();
    userDTO.setUserId(pathUserId);

    String userJson = objectMapper.writeValueAsString(userDTO);

    mockMvc
        .perform(
            patch("/users/" + user.getUserId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.message").value("Mismatch between path variable userId and userDTO id"));
  }

  @Test
  @Transactional
  public void deleteUser_ThatDoesNotExist_ThrowsNotFound() throws Exception {
    long nonExistentUserId = Long.MAX_VALUE; // Use a presumably non-existent ID

    mockMvc
        .perform(delete("/users/" + nonExistentUserId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("User not found with id: " + nonExistentUserId));
  }
}
