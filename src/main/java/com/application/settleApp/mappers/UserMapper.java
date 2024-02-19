package com.application.settleApp.mappers;

import com.application.settleApp.DTOs.UserDTO;
import com.application.settleApp.model.Cost;
import com.application.settleApp.model.Event;
import com.application.settleApp.model.User;
import java.util.stream.Collectors;

public class UserMapper implements Mapper<User, UserDTO> {

  public UserDTO toDTO(User user) {
    UserDTO dto = new UserDTO();
    dto.setUserId(user.getUserId());
    dto.setFname(user.getFname());
    dto.setLname(user.getLname());
    dto.setEventIds(user.getEvents().stream().map(Event::getEventId).collect(Collectors.toSet()));
    dto.setProductIds(user.getCosts().stream().map(Cost::getProductId).collect(Collectors.toSet()));

    return dto;
  }

  public User fromDTO(UserDTO dto) {
    User user = new User();
    user.setUserId(dto.getUserId());
    user.setFname(dto.getFname());
    user.setLname(dto.getLname());

    return user;
  }
}
