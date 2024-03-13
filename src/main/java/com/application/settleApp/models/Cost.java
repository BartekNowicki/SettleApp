package com.application.settleApp.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Cost extends BaseEntity {

  private String name;
  private Integer quantity;
  private Double unitPrice;

  // defaults to: FetchType.EAGER
  // owning side
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  // defaults to: FetchType.EAGER
  // owning side
  @ManyToOne
  @JoinColumn(name = "event_id")
  private Event event;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Cost cost = (Cost) o;
    return Objects.equals(super.getId(), cost.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.getId());
  }
}
