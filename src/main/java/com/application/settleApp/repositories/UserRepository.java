package com.application.settleApp.repositories;

import com.application.settleApp.models.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
  @Query(
      value =
          "SELECT * FROM user_table u INNER JOIN cost c ON u.user_id = c.user_id WHERE c.product_id = :productId",
      nativeQuery = true)
  Optional<User> findUserByCostsId(@Param("productId") Long productId);
}
