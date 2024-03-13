package com.application.settleApp.mappers;

import com.application.settleApp.DTOs.UserDTO;
import com.application.settleApp.models.BaseEntity;
import com.application.settleApp.models.Cost;
import com.application.settleApp.models.Event;
import com.application.settleApp.models.User;
import java.util.stream.Collectors;

public class UserMapper implements Mapper<User, UserDTO> {

  public UserDTO toDTO(User user) {
    UserDTO dto = new UserDTO();
    dto.setUserId(user.getId());
    dto.setFname(user.getFname());
    dto.setLname(user.getLname());
    dto.setEventIds(user.getEvents().stream().map(Event::getId).collect(Collectors.toSet()));
    dto.setProductIds(user.getCosts().stream().map(Cost::getId).collect(Collectors.toSet()));

    return dto;
  }

  public User fromDTO(UserDTO dto) {
    User user = BaseEntity.getNewWithDefaultDates(User.class);
    user.setId(dto.getUserId());
    user.setFname(dto.getFname());
    user.setLname(dto.getLname());

    return user;
  }
}
