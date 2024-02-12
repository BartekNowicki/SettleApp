package com.application.settleApp.controllers;

import com.application.settleApp.DTOs.CostDTO;
import com.application.settleApp.mappers.CostMapper;
import com.application.settleApp.models.Cost;
import com.application.settleApp.services.CostServiceImpl;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/costs")
public class CostController {

  private final CostServiceImpl costService;
  private final CostMapper costMapper;

  @PostMapping
  public ResponseEntity<CostDTO> createCost(@RequestBody CostDTO costDTO) {
    if (costDTO.getUserId() == null || costDTO.getEventId() == null) {
      throw new IllegalArgumentException("Both userId and eventId must be provided.");
    }
    Cost createdCost =
        costService.save(costMapper.fromDTO(costDTO), costDTO.getUserId(), costDTO.getEventId());
    return new ResponseEntity<>(costMapper.toDTO(createdCost), HttpStatus.CREATED);
  }

  @GetMapping("/{costId}")
  public ResponseEntity<CostDTO> getCostById(@PathVariable Long costId) {
    Cost cost = costService.findById(costId);
    return ResponseEntity.ok(costMapper.toDTO(cost));
  }

  @GetMapping
  public ResponseEntity<List<CostDTO>> getAllCosts() {
    List<Cost> costs = costService.findAll();
    List<CostDTO> costDTOs = costs.stream().map(costMapper::toDTO).collect(Collectors.toList());
    return ResponseEntity.ok(costDTOs);
  }

  @PatchMapping("/{costId}")
  public ResponseEntity<CostDTO> updateCost(
      @PathVariable Long costId, @RequestBody CostDTO costDTO) {
    if (costDTO.getProductId() != null && !Objects.equals(costId, costDTO.getProductId())) {
      throw new IllegalArgumentException("Mismatch between path variable costId and costDTO id");
    }
    Cost updatedCost =
        costService.save(costMapper.fromDTO(costDTO), costDTO.getUserId(), costDTO.getEventId());
    return ResponseEntity.ok(costMapper.toDTO(updatedCost));
  }

  @DeleteMapping("/{costId}")
  public ResponseEntity<CostDTO> deleteCost(@PathVariable Long costId) {
    Cost deletedCost = costService.deleteById(costId);
    CostDTO deletedCostDTO = costMapper.toDTO(deletedCost);
    return ResponseEntity.ok(deletedCostDTO);
  }
}
