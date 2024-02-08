package com.application.settleApp.DTOs;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDTO {
  private Long userId;
  private String fname;
  private String lname;
  private Set<Long> eventIds;
  private Set<Long> productIds;
}
