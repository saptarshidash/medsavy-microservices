package com.medsavy.medsavyinventorymanagementservice.dto;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medicine {

  private Integer batchId;

  private String name;

  private String type;

  private Double price;

  private Date expiryDate;

  private Integer quantity;
}
