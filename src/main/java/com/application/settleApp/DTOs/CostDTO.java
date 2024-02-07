package com.application.settleApp.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CostDTO {
  private Long productId;
  private String name;
  private Integer quantity;
  private Double unitPrice;
  private Long userId;
  private Long eventId;
}
