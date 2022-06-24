package com.medsavy.medsavyinventorymanagementservice.repository;

import com.medsavy.medsavyinventorymanagementservice.entity.MedInventoryEntity;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InventoryRepository extends JpaRepository<MedInventoryEntity, Integer> {

  @Query(value = "SELECT u FROM MedInventoryEntity u WHERE u.inventoryId = ?1 AND (u.name LIKE %?2% OR u.type LIKE %?2%)")
  List<MedInventoryEntity> findMedInventoryEntitiesByInventoryIdAndSearchString(
      Integer inventoryId,
      String searchString);

  List<MedInventoryEntity> findMedInventoryEntitiesByInventoryId(Integer IV);

  MedInventoryEntity findMedInventoryEntityByNameAndAndExpiryDateAndInventoryId(
      String name,
      String expDate,
      Integer inventoryId);


}
