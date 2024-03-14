package com.application.settleApp.models;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
// @Table(name = "user") user is a reserved word and needs to be set as follows otherwise h2 tests throw sql syntax errors:
@Table(name = "`user`")
public class User extends BaseEntity {

  private String fname;
  private String lname;
  private String email;
  private String password;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "user_role",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return Objects.equals(super.getId(), user.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.getId());
  }
}
