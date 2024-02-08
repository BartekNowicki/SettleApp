package com.application.settleApp.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Cost {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long productId;

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
}
