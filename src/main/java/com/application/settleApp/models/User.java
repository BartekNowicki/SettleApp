package com.application.settleApp.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "user_table")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId;

  private String fname;
  private String lname;

  // owning side: Event
  @ManyToMany(mappedBy = "participants")
  private Set<Event> events = new HashSet<>();

  @OneToMany(mappedBy = "user")
  private Set<Cost> costs = new HashSet<>();

  public void addCost(Cost cost) {
    this.costs.add(cost);
    cost.setUser(this);
  }

  public void addEvent(Event event) {
    events.add(event);
    event.getParticipants().add(this);
  }
}
