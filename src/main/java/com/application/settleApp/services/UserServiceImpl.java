package com.application.settleApp.services;

import com.application.settleApp.models.User;
import com.application.settleApp.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Override
  public User findById(Long id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
  }

  @Override
  public User save(User user) {
    return userRepository.save(user);
  }

  @Override
  public List<User> findAll() {
    return userRepository.findAll();
  }

  @Override
  public User delete(User user) {
    if (user == null) {
      throw new IllegalArgumentException("Cannot delete a null user.");
    }
    userRepository.delete(user);
    return user;
  }

  @Override
  public User deleteById(Long id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    userRepository.delete(user);

    return user;
  }
}
