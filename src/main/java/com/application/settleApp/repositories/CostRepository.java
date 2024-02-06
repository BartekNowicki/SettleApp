package com.application.settleApp.repositories;

import com.application.settleApp.models.Cost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CostRepository extends JpaRepository<Cost, Long> {}
