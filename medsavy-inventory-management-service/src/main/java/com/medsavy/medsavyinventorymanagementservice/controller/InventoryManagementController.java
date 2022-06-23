package com.medsavy.medsavyinventorymanagementservice.controller;

import com.medsavy.medsavyinventorymanagementservice.exchanges.AddMedRequest;
import com.medsavy.medsavyinventorymanagementservice.exchanges.AddMedResponse;
import com.medsavy.medsavyinventorymanagementservice.exchanges.CreateInventoryRequest;
import com.medsavy.medsavyinventorymanagementservice.exchanges.CreateInventoryResponse;
import com.medsavy.medsavyinventorymanagementservice.exchanges.GetMedResponse;
import com.medsavy.medsavyinventorymanagementservice.services.InventoryManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InventoryManagementController {

  private final InventoryManagementService inventoryManagementService;

  @PostMapping("/inventory/medicine")
  public AddMedResponse addMedicine(@RequestBody @Validated AddMedRequest request) {
    return inventoryManagementService.addMedInInventory(request);
  }

  @PostMapping("/inventory")
  public CreateInventoryResponse createInventoryForUser(@RequestBody CreateInventoryRequest request) {
    return inventoryManagementService.createInventoryForUser(request.getUserId());
  }

  @GetMapping("/inventory/{inventoryId}/batch/medicines")
  public GetMedResponse getMedicineBatchesByName(@PathVariable Integer inventoryId,
      @RequestParam String name) {
    return inventoryManagementService.getMedicinesByName(name, inventoryId);
  }

  @GetMapping("/inventory/{inventoryId}/medicines")
  public GetMedResponse getAllMedicinesFromInventory(@PathVariable Integer inventoryId) {
    return inventoryManagementService.getAllMedicinesByInventoryId(inventoryId);
  }
}
