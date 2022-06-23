package com.medsavy.medsavyinventorymanagementservice.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MedicineInventory")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MedInventoryEntity {



  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer batchId;

  private Integer inventoryId;

  private String name;

  private String type;

  private Double price;

  private Date expiryDate;

  private Integer quantity;

  public MedInventoryEntity(Integer inventoryId, String name, String type, Double price,
      Date expiryDate, Integer quantity) {

    this.inventoryId = inventoryId;
    this.name = name;
    this.type = type;
    this.price = price;
    this.expiryDate = expiryDate;
    this.quantity = quantity;
  }
}
