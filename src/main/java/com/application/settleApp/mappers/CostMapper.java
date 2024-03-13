package com.application.settleApp.mappers;

import com.application.settleApp.DTOs.CostDTO;
import com.application.settleApp.models.BaseEntity;
import com.application.settleApp.models.Cost;
import com.application.settleApp.models.Event;
import com.application.settleApp.models.User;
import java.util.Optional;

public class CostMapper implements Mapper<Cost, CostDTO> {

  public CostDTO toDTO(Cost cost) {
    CostDTO dto = new CostDTO();
    dto.setProductId(cost.getId());
    dto.setName(cost.getName());
    dto.setQuantity(cost.getQuantity());
    dto.setUnitPrice(cost.getUnitPrice());
    dto.setUserId(Optional.ofNullable(cost.getUser()).map(User::getId).orElse(null));
    dto.setEventId(Optional.ofNullable(cost.getEvent()).map(Event::getId).orElse(null));

    return dto;
  }

  public Cost fromDTO(CostDTO dto) {
    Cost cost = BaseEntity.getNewWithDefaultDates(Cost.class);
    cost.setId(dto.getProductId());
    cost.setName(dto.getName());
    cost.setQuantity(dto.getQuantity());
    cost.setUnitPrice(dto.getUnitPrice());

    return cost;
  }
}
