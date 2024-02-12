package com.application.settleApp.repositories;

import com.application.settleApp.models.Event;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventRepository extends JpaRepository<Event, Long> {
  @Query(
      value =
          "SELECT * FROM event e INNER JOIN cost c ON e.event_id = c.event_id WHERE c.product_id = :productId",
      nativeQuery = true)
  Optional<Event> findEventByCostsId(@Param("productId") Long productId);
}
