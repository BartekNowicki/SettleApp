package com.application.settleApp.models;

import com.application.settleApp.enums.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
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
public class Event {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long eventId;

  @Enumerated(EnumType.STRING)
  private Status status;

  private LocalDate eventDate;
  private Long createdByUserId;

  // owning side
  @ManyToMany
  @JoinTable(
      name = "participants",
      joinColumns = @JoinColumn(name = "event_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id"))
  private Set<User> participants = new HashSet<>();

  @OneToMany(mappedBy = "event")
  private Set<Cost> costs = new HashSet<>();

  public void addCost(Cost cost) {
    this.costs.add(cost);
    cost.setEvent(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Event event = (Event) o;
    return Objects.equals(eventId, event.eventId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(eventId);
  }
}
