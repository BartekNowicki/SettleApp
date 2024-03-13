package com.application.settleApp.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.application.settleApp.DTOs.UserDTO;
import com.application.settleApp.models.BaseEntity;
import com.application.settleApp.models.Role;
import com.application.settleApp.models.User;
import com.application.settleApp.repositories.UserRepository;
import com.application.settleApp.security.AuthRequest;
import com.application.settleApp.services.JwtTokenService;
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
public class UserControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private UserRepository userRepository;
  @Autowired private JwtTokenService jwtTokenService;
  @Autowired private JdbcTemplate jdbcTemplate;

  private String userToken;

  @BeforeEach
  public void setup() {
    userRepository.deleteAll();

    String insertRoleSql =
        "INSERT INTO role (role_id, name)\n"
            + "SELECT 1, 'USER'\n"
            + "WHERE NOT EXISTS (\n"
            + "  SELECT 1 FROM role WHERE role_id = 1\n"
            + ");";
    jdbcTemplate.update(insertRoleSql);

    User userMakingRequests = BaseEntity.getNewWithDefaultDates(User.class);
    userMakingRequests.setEmail("userMakingRequests@example.com");
    String passwordNotHashed = "hashed_password1";
    String passwordHashedStoredInDb =
        "$2b$12$iU/7c.jaS5Ze57mBdxXMUuGrkhjOzeZ3ZZVF6mA6nZMAdUv57jnuK";
    userMakingRequests.setPassword(passwordHashedStoredInDb);
    Role userMakingRequestsRole = new Role();
    // the id of this role matches the above sql insertions to comply with foreign key constraints
    userMakingRequestsRole.setRoleId(1L);
    userMakingRequestsRole.setName("USER");
    userMakingRequests.getRoles().add(userMakingRequestsRole);
    userRepository.save(userMakingRequests);

    userToken =
        "Bearer "
            + jwtTokenService.generateToken(
                new AuthRequest(userMakingRequests.getEmail(), passwordNotHashed));
  }

  private HttpHeaders getAuthorizationHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", userToken);
    return headers;
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
                .headers(getAuthorizationHeaders())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.fname").value(newUser.getFname()));
  }

  @Test
  public void createUser_WithId_ThrowsIllegalArgumentException() throws Exception {
    UserDTO newUser = new UserDTO();
    newUser.setUserId(1L);
    newUser.setFname("New");
    newUser.setLname("User");

    mockMvc
        .perform(
            post("/users")
                .headers(getAuthorizationHeaders())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
        .andExpect(status().isBadRequest())
        .andExpect(
            content().string(containsString("Id is autoincremented and should not be provided")));
  }

  @Test
  @Transactional
  public void getAllUsers_Success() throws Exception {
    mockMvc
        .perform(get("/users").headers(getAuthorizationHeaders()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  @Test
  @Transactional
  public void updateUser_Success() throws Exception {
    User user = BaseEntity.getNewWithDefaultDates(User.class);
    user.setFname("Initial Name");
    User savedUser = userRepository.save(user);

    UserDTO userDTO = new UserDTO();
    userDTO.setUserId(savedUser.getId());
    userDTO.setFname("Updated Name");

    mockMvc
        .perform(
            patch("/users/" + savedUser.getId())
                .headers(getAuthorizationHeaders())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.fname").value("Updated Name"));
  }

  @Test
  @Transactional
  public void deleteUser_Success() throws Exception {
    User user = BaseEntity.getNewWithDefaultDates(User.class);
    user.setFname("To Be Deleted");
    User savedUser = userRepository.save(user);

    mockMvc
        .perform(delete("/users/" + savedUser.getId()).headers(getAuthorizationHeaders()))
        .andExpect(status().isOk());

    mockMvc
        .perform(get("/users/" + savedUser.getId()).headers(getAuthorizationHeaders()))
        .andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  public void updateUser_MismatchUserId_ThrowsException() throws Exception {
    User user = userRepository.save(BaseEntity.getNewWithDefaultDates(User.class));
    long pathUserId = user.getId() + 999;

    UserDTO userDTO = new UserDTO();
    userDTO.setUserId(pathUserId);

    String userJson = objectMapper.writeValueAsString(userDTO);

    mockMvc
        .perform(
            patch("/users/" + user.getId())
                .headers(getAuthorizationHeaders())
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
        .perform(delete("/users/" + nonExistentUserId).headers(getAuthorizationHeaders()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("User not found with id: " + nonExistentUserId));
  }
}
