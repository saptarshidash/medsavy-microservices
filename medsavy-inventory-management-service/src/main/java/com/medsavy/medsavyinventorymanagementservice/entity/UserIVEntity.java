package com.medsavy.medsavyinventorymanagementservice.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "UserInventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserIVEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer inventoryId;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "id")
  private UserEntity userEntity;
}
