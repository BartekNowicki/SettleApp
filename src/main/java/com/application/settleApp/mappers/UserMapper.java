package com.application.settleApp.mappers;

import com.application.settleApp.DTOs.UserDTO;
import com.application.settleApp.models.Cost;
import com.application.settleApp.models.Event;
import com.application.settleApp.models.User;
import java.util.stream.Collectors;

public class UserMapper {

  public static UserDTO toDTO(User user) {
    UserDTO dto = new UserDTO();
    dto.setUserId(user.getUserId());
    dto.setFname(user.getFname());
    dto.setLname(user.getLname());
    dto.setEventIds(user.getEvents().stream().map(Event::getEventId).collect(Collectors.toSet()));
    dto.setCostIds(user.getCosts().stream().map(Cost::getProductId).collect(Collectors.toSet()));

    return dto;
  }

  public static User fromDTO(UserDTO dto) {
    User user = new User();
    user.setUserId(dto.getUserId());
    user.setFname(dto.getFname());
    user.setLname(dto.getLname());

    return user;
  }
}
