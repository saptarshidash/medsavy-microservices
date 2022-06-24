package com.medsavy.medsavyinventorymanagementservice.services;

import com.medsavy.medsavyinventorymanagementservice.exchanges.AddMedRequest;
import com.medsavy.medsavyinventorymanagementservice.exchanges.AddMedResponse;
import com.medsavy.medsavyinventorymanagementservice.exchanges.CreateInventoryResponse;
import com.medsavy.medsavyinventorymanagementservice.exchanges.GetMedResponse;
import com.medsavy.medsavyinventorymanagementservice.exchanges.GetSalesResponse;
import com.medsavy.medsavyinventorymanagementservice.exchanges.IVUpdateRequest;
import com.medsavy.medsavyinventorymanagementservice.exchanges.IVUpdateResponse;

public interface InventoryManagementService {

  AddMedResponse addMedInInventory(AddMedRequest request);

  CreateInventoryResponse createInventoryForUser(Integer userId);

  GetMedResponse getMedicinesBySearchString(String searchString, Integer inventoryId);

  GetMedResponse getAllMedicinesByInventoryId(Integer inventoryId);

  IVUpdateResponse updateMedInInventoryAfterSell(Integer inventoryId, IVUpdateRequest updateRequest);

  GetSalesResponse getSalesDataBySalesId(Integer salesId);
}
