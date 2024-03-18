package com.application.settleApp.services;

import com.application.settleApp.enums.ExceptionMessage;
import com.application.settleApp.models.User;
import com.application.settleApp.repositories.CostRepository;
import com.application.settleApp.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final CostRepository costRepository;

  @Override
  public User findById(Long id) {
    return userRepository
        .findById(id)
        .orElseThrow(
            () -> new EntityNotFoundException(ExceptionMessage.USER_NOT_FOUND.getMessage() + id));
  }

  @Override
  public User save(User user) {
    return userRepository.save(user);
  }

  @Override
  public List<User> findAll() {
    return userRepository.findAll();
  }

  private void removeUserFromCostsAndEvents(User user) {
    user.getEvents().forEach(event -> event.getParticipants().remove(user));
    user.getEvents().clear();
    costRepository.deleteAll(user.getCosts());
    user.getCosts().clear();
  }

  @Override
  @Transactional
  public User delete(User user) {
    if (user == null) {
      throw new IllegalArgumentException(ExceptionMessage.CANNOT_DELETE_NULL_USER.getMessage());
    }
    removeUserFromCostsAndEvents(user);
    userRepository.delete(user);
    return user;
  }

  @Override
  @Transactional
  public User deleteById(Long id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    removeUserFromCostsAndEvents(user);
    userRepository.delete(user);

    return user;
  }
}
