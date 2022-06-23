package com.medsavy.medsavyinventorymanagementservice.exchanges;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateInventoryResponse {

  private Integer inventoryId;
  private String message;
}
