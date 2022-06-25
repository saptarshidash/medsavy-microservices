package com.medsavy.medsavyinventorymanagementservice.exchanges;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddMedResponse {

  private Integer batchId;

  private String name;

  private Integer inventoryId;

  private String message;

  private Boolean success;
}
