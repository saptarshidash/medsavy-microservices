package com.medsavy.medsavyinventorymanagementservice.repository;

import com.medsavy.medsavyinventorymanagementservice.entity.UserEntity;
import com.medsavy.medsavyinventorymanagementservice.entity.UserInventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserInventoryRepository extends JpaRepository<UserInventoryEntity, Integer> {

  @Query(value = "SELECT u FROM UserInventoryEntity u WHERE u.userEntity.userId = ?1")
  UserInventoryEntity findUserInventoryEntityByUserId(Integer id);

  Boolean existsByUserEntity(UserEntity userEntity);
}
