package com.application.settleApp.mappers;

import com.application.settleApp.DTOs.CostDTO;
import com.application.settleApp.models.Cost;
import com.application.settleApp.models.Event;
import com.application.settleApp.models.User;
import java.util.Optional;

public class CostMapper {

  public static CostDTO toDTO(Cost cost) {
    CostDTO dto = new CostDTO();
    dto.setProductId(cost.getProductId());
    dto.setName(cost.getName());
    dto.setQuantity(cost.getQuantity());
    dto.setUnitPrice(cost.getUnitPrice());
    dto.setUserId(Optional.ofNullable(cost.getUser()).map(User::getUserId).orElse(null));
    dto.setEventId(Optional.ofNullable(cost.getEvent()).map(Event::getEventId).orElse(null));

    return dto;
  }

  public static Cost fromDTO(CostDTO dto, User user, Event event) {
    Cost cost = new Cost();
    cost.setProductId(dto.getProductId());
    cost.setName(dto.getName());
    cost.setQuantity(dto.getQuantity());
    cost.setUnitPrice(dto.getUnitPrice());
    cost.setUser(user);
    cost.setEvent(event);

    return cost;
  }
}
