package com.application.settleApp.controllers;

import com.application.settleApp.DTOs.UserDTO;
import com.application.settleApp.enums.ExceptionMessage;
import com.application.settleApp.mappers.UserMapper;
import com.application.settleApp.models.User;
import com.application.settleApp.services.UserServiceImpl;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

  private final UserServiceImpl userService;
  private final UserMapper userMapper;

  @PostMapping
  public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
    if (userDTO.getUserId() != null) {
      throw new IllegalArgumentException(ExceptionMessage.ID_AUTOINCREMENTED.getMessage());
    }
    User createdUser = userService.save(userMapper.fromDTO(userDTO));
    return new ResponseEntity<>(userMapper.toDTO(createdUser), HttpStatus.CREATED);
  }

  @GetMapping("/{userId}")
  public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
    User user = userService.findById(userId);
    return ResponseEntity.ok(userMapper.toDTO(user));
  }

  @GetMapping
  public ResponseEntity<List<UserDTO>> getAllUsers() {
    List<User> users = userService.findAll();
    List<UserDTO> userDTOs = users.stream().map(userMapper::toDTO).collect(Collectors.toList());
    return ResponseEntity.ok(userDTOs);
  }

  @PatchMapping("/{userId}")
  public ResponseEntity<UserDTO> updateUser(
      @PathVariable Long userId, @RequestBody UserDTO userDTO) {
    if (userDTO.getUserId() != null && !Objects.equals(userId, userDTO.getUserId())) {
      throw new IllegalArgumentException(ExceptionMessage.MISMATCH_USER_ID.getMessage());
    }
    User user = userMapper.fromDTO(userDTO);
    User updatedUser = userService.save(user);
    return ResponseEntity.ok(userMapper.toDTO(updatedUser));
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<UserDTO> deleteUser(@PathVariable Long userId) {
    User deletedUser = userService.deleteById(userId);
    UserDTO deletedUserDTO = userMapper.toDTO(deletedUser);
    return ResponseEntity.ok(deletedUserDTO);
  }
}
