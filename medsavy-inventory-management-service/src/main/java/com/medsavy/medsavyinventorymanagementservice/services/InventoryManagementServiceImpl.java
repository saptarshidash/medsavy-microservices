package com.medsavy.medsavyinventorymanagementservice.services;

import static com.medsavy.medsavyinventorymanagementservice.utils.InventoryConstants.INVENTORY_CREATED;
import static com.medsavy.medsavyinventorymanagementservice.utils.InventoryConstants.INVENTORY_CREATION_FAILURE;
import static com.medsavy.medsavyinventorymanagementservice.utils.InventoryConstants.MEDICINE_ADD_FAILURE;
import static com.medsavy.medsavyinventorymanagementservice.utils.InventoryConstants.MEDICINE_ADD_SUCCESS;

import com.medsavy.medsavyinventorymanagementservice.dto.Medicine;
import com.medsavy.medsavyinventorymanagementservice.entity.MedInventoryEntity;
import com.medsavy.medsavyinventorymanagementservice.entity.UserEntity;
import com.medsavy.medsavyinventorymanagementservice.entity.UserInventoryEntity;
import com.medsavy.medsavyinventorymanagementservice.enums.Transaction;
import com.medsavy.medsavyinventorymanagementservice.exchanges.AddMedRequest;
import com.medsavy.medsavyinventorymanagementservice.exchanges.AddMedResponse;
import com.medsavy.medsavyinventorymanagementservice.exchanges.CreateInventoryResponse;
import com.medsavy.medsavyinventorymanagementservice.exchanges.GetMedResponse;
import com.medsavy.medsavyinventorymanagementservice.repository.InventoryRepository;
import com.medsavy.medsavyinventorymanagementservice.repository.UserInventoryRepository;
import com.medsavy.medsavyinventorymanagementservice.repository.UserRepository;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InventoryManagementServiceImpl implements InventoryManagementService{


  private final InventoryRepository inventoryRepository;

  private final UserInventoryRepository userInventoryRepository;

  private final UserRepository userRepository;

  private final ModelMapper modelMapper;



  @Override
  public AddMedResponse addMedInInventory(AddMedRequest request) {

    AddMedResponse response = new AddMedResponse();
    UserInventoryEntity userInventoryEntity = userInventoryRepository
        .findUserInventoryEntityByUserId(request.getUserId());

    // Check medicine with same name , expiry date exist or not
    // If exist than update the batch by increasing qty and setting the recent price
    // else create a new batch with the med

    MedInventoryEntity medIVEntity = insertOrUpdateMedIVEntity(
        userInventoryEntity.getInventoryId(), request
    );


    inventoryRepository.save(medIVEntity);

    if(medIVEntity.getBatchId() != null) {
      response = modelMapper.map(medIVEntity, AddMedResponse.class);
      response.setMessage(MEDICINE_ADD_SUCCESS);
      return response;
    }

    response.setMessage(MEDICINE_ADD_FAILURE);
    return response;
  }

  private MedInventoryEntity insertOrUpdateMedIVEntity(Integer ivID, AddMedRequest request) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String expDate = dateFormat.format(request.getExpiryDate());

    MedInventoryEntity medIVEntity = inventoryRepository
        .findMedInventoryEntityByNameAndAndExpiryDateAndInventoryId(
            request.getName(), expDate, ivID);

    if(medIVEntity != null) {
      medIVEntity.increaseQuantity(request.getQuantity());
      medIVEntity.updatePrice(request.getPrice());
      log.info("inside [insertOrUpdateMedIVEntity] Updating existing medicine batch {} in "
              + "inventory {}", medIVEntity.getBatchId(), ivID);

      return inventoryRepository.save(medIVEntity);
    }

    // Constructing new medicine batch
    log.info("inside [insertOrUpdateMedIVEntity] Constructing new medicine batch in inventory {}",
        ivID);

    medIVEntity = new MedInventoryEntity(
        ivID,
        request.getName(),
        request.getType(),
        request.getPrice(),
        expDate,
        request.getQuantity(),
        Transaction.PURCHASE.name()
    );

    return medIVEntity;
  }

  @Override
  public CreateInventoryResponse createInventoryForUser(Integer userId) {

    UserEntity userEntity = null;
    CreateInventoryResponse response = new CreateInventoryResponse();
    UserInventoryEntity userInventoryEntity = new UserInventoryEntity();
    Optional<UserEntity> optionalUserEntity = userRepository.findById(userId);

    if(optionalUserEntity.isPresent()) {
      userEntity = optionalUserEntity.get();
      log.info("inside [createInventoryForUser] creating inventory for user {}",
          userEntity.getUserName());

      if(userInventoryRepository.existsByUserEntity(userEntity)) {
        log.info("inside [createInventoryForUser] unable to create inventory for user {}"
            + " inventory already exists", userEntity.getUserName());
        response.setMessage(INVENTORY_CREATION_FAILURE);
        return response;
      }

      userInventoryEntity.setUserEntity(userEntity);
      userInventoryRepository.save(userInventoryEntity);
      response = modelMapper.map(userInventoryEntity, CreateInventoryResponse.class);
      response.setMessage(INVENTORY_CREATED);
      return response;
    }

    if(optionalUserEntity.isEmpty()) {
      log.info("Unable to create inventory. No user found with id {}", userId);
      response.setMessage(INVENTORY_CREATION_FAILURE);
    }

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
