package com.medsavy.medsavyinventorymanagementservice.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medicine {

  private String name;

  private String type;

  private Double price;

  private int totalQty;

  private List<Batch> batch = new ArrayList<>();

  public Medicine(String name, String type, Double price) {
    this.name = name;
    this.type = type;
    this.price = price;
  }

  public void addBatch(Batch b) {
    batch.add(b);
  }
}
