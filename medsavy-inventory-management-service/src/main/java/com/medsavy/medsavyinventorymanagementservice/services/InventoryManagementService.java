package com.medsavy.medsavyinventorymanagementservice.services;

import com.medsavy.medsavyinventorymanagementservice.exchanges.AddMedRequest;
import com.medsavy.medsavyinventorymanagementservice.exchanges.AddMedResponse;
import com.medsavy.medsavyinventorymanagementservice.exchanges.CreateInventoryResponse;
import com.medsavy.medsavyinventorymanagementservice.exchanges.GetMedResponse;

public interface InventoryManagementService {

  AddMedResponse addMedInInventory(AddMedRequest request);

  CreateInventoryResponse createInventoryForUser(Integer userId);

  GetMedResponse getMedicinesByName(String searchString, Integer inventoryId);

  GetMedResponse getAllMedicinesByInventoryId(Integer inventoryId);
}
