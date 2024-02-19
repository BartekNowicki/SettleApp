package com.application.settleApp.mappers;

import com.application.settleApp.DTOs.CostDTO;
import com.application.settleApp.model.Cost;
import com.application.settleApp.model.Event;
import com.application.settleApp.model.User;
import java.util.Optional;

public class CostMapper implements Mapper<Cost, CostDTO> {

  public CostDTO toDTO(Cost cost) {
    CostDTO dto = new CostDTO();
    dto.setProductId(cost.getProductId());
    dto.setName(cost.getName());
    dto.setQuantity(cost.getQuantity());
    dto.setUnitPrice(cost.getUnitPrice());
    dto.setUserId(Optional.ofNullable(cost.getUser()).map(User::getUserId).orElse(null));
    dto.setEventId(Optional.ofNullable(cost.getEvent()).map(Event::getEventId).orElse(null));

    return dto;
  }

  public Cost fromDTO(CostDTO dto) {
    Cost cost = new Cost();
    cost.setProductId(dto.getProductId());
    cost.setName(dto.getName());
    cost.setQuantity(dto.getQuantity());
    cost.setUnitPrice(dto.getUnitPrice());

    return cost;
  }
}
