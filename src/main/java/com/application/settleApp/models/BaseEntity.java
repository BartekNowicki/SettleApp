package com.application.settleApp.models;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private LocalDateTime creationDate;

  @Column(nullable = false)
  private LocalDateTime modificationDate;

  // Static method to create a new instance with default dates, needed to simplify tests
  public static <T extends BaseEntity> T getNewWithDefaultDates(Class<T> entityClass) {
    try {
      T entity = entityClass.getDeclaredConstructor().newInstance();
      LocalDateTime now = LocalDateTime.now();
      entity.setCreationDate(now);
      entity.setModificationDate(now);
      return entity;
    } catch (Exception e) {
      throw new RuntimeException("Failed to create instance of " + entityClass.getName(), e);
    }
  }
}
