package com.application.settleApp.DTOs;

import com.application.settleApp.enums.Status;
import java.time.LocalDate;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventDTO {
  private Long eventId;
  private Status status;
  private LocalDate eventDate;
  private Long createdByUserId;
  private Set<Long> participantIds;
}
