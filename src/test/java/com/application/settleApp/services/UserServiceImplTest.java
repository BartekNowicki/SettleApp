package com.application.settleApp.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.application.settleApp.models.BaseEntity;
import com.application.settleApp.models.User;
import com.application.settleApp.repositories.CostRepository;
import com.application.settleApp.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserServiceImplTest {

  @Mock private UserRepository userRepository;
  @Mock private CostRepository costRepository;

  @InjectMocks private UserServiceImpl userService;

  private User user1 = BaseEntity.getNewWithDefaultDates(User.class);
  private User user2 = BaseEntity.getNewWithDefaultDates(User.class);
  private User user3 = BaseEntity.getNewWithDefaultDates(User.class);
  List<User> userList = new ArrayList<>();

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    user1.setId(1L);
    user2.setId(2L);
    user3.setId(3L);
    userList.add(user1);
    userList.add(user2);
    userList.add(user3);
  }

  @Test
  void testFindById() {
    user1.setId(1L);

    when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

    User found = userService.findById(1L);

    assertEquals(1L, found.getId());
    verify(userRepository).findById(1L);
  }

  @Test
  void testFindById_NotFound() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> userService.findById(1L));
  }

  @Test
  void testSave() {
    User user99 = BaseEntity.getNewWithDefaultDates(User.class);
    user99.setId(99L);

    when(userRepository.save(user99)).thenReturn(user99);

    User savedUser = userService.save(user99);

    verify(userRepository).save(user99);

    assertNotNull(savedUser);
    assertEquals(99L, savedUser.getId());
  }

  @Test
  void testFindAll() {

    when(userRepository.findAll()).thenReturn(userList);

    List<User> retrievedUsers = userService.findAll();

    verify(userRepository).findAll();

    assertNotNull(retrievedUsers);
    assertEquals(3, retrievedUsers.size());
  }

  @Test
  void testDelete() {

    userService.delete(user1);

    verify(userRepository).delete(user1);
  }

  @Test
  void testDeleteById() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

    userService.deleteById(1L);

    verify(userRepository).delete(user1);
  }
}
