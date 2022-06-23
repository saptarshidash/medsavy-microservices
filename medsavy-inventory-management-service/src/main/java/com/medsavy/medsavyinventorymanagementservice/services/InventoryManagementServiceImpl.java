package com.medsavy.medsavyinventorymanagementservice.services;

import com.medsavy.medsavyinventorymanagementservice.dto.Medicine;
import com.medsavy.medsavyinventorymanagementservice.entity.MedInventoryEntity;
import com.medsavy.medsavyinventorymanagementservice.entity.UserEntity;
import com.medsavy.medsavyinventorymanagementservice.entity.UserInventoryEntity;
import com.medsavy.medsavyinventorymanagementservice.exchanges.AddMedRequest;
import com.medsavy.medsavyinventorymanagementservice.exchanges.AddMedResponse;
import com.medsavy.medsavyinventorymanagementservice.exchanges.CreateInventoryResponse;
import com.medsavy.medsavyinventorymanagementservice.exchanges.GetMedResponse;
import com.medsavy.medsavyinventorymanagementservice.repository.InventoryRepository;
import com.medsavy.medsavyinventorymanagementservice.repository.UserInventoryRepository;
import com.medsavy.medsavyinventorymanagementservice.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InventoryManagementServiceImpl implements InventoryManagementService{


  private final InventoryRepository inventoryRepository;

  private final UserInventoryRepository userInventoryRepository;

  private final UserRepository userRepository;

  private final ModelMapper modelMapper;



  @Override
  public AddMedResponse addMedInInventory(AddMedRequest request) {

    AddMedResponse response = null;
    UserInventoryEntity userInventoryEntity = userInventoryRepository
        .findUserInventoryEntityByUserId(request.getUserId());

    MedInventoryEntity medIVEntity = new MedInventoryEntity(
        userInventoryEntity.getInventoryId(),
        request.getName(),
        request.getType(),
        request.getPrice(),
        request.getExpiryDate(),
        request.getQuantity()
    );

    inventoryRepository.save(medIVEntity);
    if(medIVEntity.getBatchId() != null) {
      response = modelMapper.map(medIVEntity, AddMedResponse.class);
      response.setMessage("Medicine Added Successfully");
      return response;
    }

    response = modelMapper.map(medIVEntity, AddMedResponse.class);
    response.setMessage("Unable to add medicine");
    return response;
  }

  @Override
  public CreateInventoryResponse createInventoryForUser(Integer userId) {

    UserEntity userEntity = null;
    CreateInventoryResponse response = new CreateInventoryResponse();
    UserInventoryEntity userInventoryEntity = new UserInventoryEntity();
    Optional<UserEntity> optionalUserEntity = userRepository.findById(userId);

    if(optionalUserEntity.isPresent()) {
      userEntity = optionalUserEntity.get();
      userInventoryEntity.setUserEntity(userEntity);
      userInventoryRepository.save(userInventoryEntity);
      response = modelMapper.map(userInventoryEntity, CreateInventoryResponse.class);
      response.setMessage("Inventory created successfully");
      return response;
    }

    response.setMessage("Inventory creation failed");
    return response;
  }

  @Override
  public GetMedResponse getMedicinesByName(String searchString, Integer inventoryId) {
    List<MedInventoryEntity> medIVEntities = inventoryRepository
        .findMedInventoryEntitiesByNameAndInventoryId(searchString, inventoryId);

    List<Medicine> medicines = medIVEntities.stream()
        .map(medIVEntity -> modelMapper.map(medIVEntity, Medicine.class))
        .toList();

    return new GetMedResponse(medicines);
  }

  @Override
  public GetMedResponse getAllMedicinesByInventoryId(Integer inventoryId) {
    List<MedInventoryEntity> medIVEntities = inventoryRepository
        .findMedInventoryEntitiesByInventoryId(inventoryId);

    List<Medicine> medicineList = medIVEntities.stream()
        .map(medIVEntity -> modelMapper.map(medIVEntity, Medicine.class))
        .toList();
    return new GetMedResponse(medicineList);
  }
}
