package com.application.settleApp.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
public class Role {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long roleId;

  @Column(unique = true)
  private String name;

  // owning side: User
  @ManyToMany(mappedBy = "roles")
  private Set<User> users;
}
