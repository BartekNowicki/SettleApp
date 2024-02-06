package com.application.settleApp.services;

import com.application.settleApp.models.User;
import com.application.settleApp.repositories.UserRepository;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Override
  public User findById(Long id) {
    return userRepository.findById(id).orElse(null);
  }

  @Override
  public User save(User object) {
    return userRepository.save(object);
  }

  @Override
  public Set<User> findAll() {
      return new HashSet<>(userRepository.findAll());
  }

  @Override
  public User delete(User object) {
    userRepository.delete(object);
    return object;
  }

  @Override
  public Long deleteById(Long id) {
    userRepository.deleteById(id);
    return id;
  }
}
