package com.medsavy.medsavyinventorymanagementservice.repository;

import com.medsavy.medsavyinventorymanagementservice.entity.MedInventoryEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<MedInventoryEntity, Integer> {
  List<MedInventoryEntity> findMedInventoryEntitiesByNameAndInventoryId(String name, Integer IV);

  List<MedInventoryEntity> findMedInventoryEntitiesByInventoryId(Integer IV);
}
