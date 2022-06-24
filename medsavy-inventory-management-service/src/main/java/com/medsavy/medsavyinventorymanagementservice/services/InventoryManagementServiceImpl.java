package com.medsavy.medsavyinventorymanagementservice.services;

import static com.medsavy.medsavyinventorymanagementservice.enums.Transaction.SELL;
import static com.medsavy.medsavyinventorymanagementservice.utils.InventoryConstants.INVENTORY_CREATED;
import static com.medsavy.medsavyinventorymanagementservice.utils.InventoryConstants.INVENTORY_CREATION_FAILURE;
import static com.medsavy.medsavyinventorymanagementservice.utils.InventoryConstants.MEDICINE_ADD_FAILURE;
import static com.medsavy.medsavyinventorymanagementservice.utils.InventoryConstants.MEDICINE_ADD_SUCCESS;

import com.medsavy.medsavyinventorymanagementservice.dto.Batch;
import com.medsavy.medsavyinventorymanagementservice.dto.Medicine;
import com.medsavy.medsavyinventorymanagementservice.entity.MedInventoryEntity;
import com.medsavy.medsavyinventorymanagementservice.entity.SalesEntity;
import com.medsavy.medsavyinventorymanagementservice.entity.UserEntity;
import com.medsavy.medsavyinventorymanagementservice.entity.UserInventoryEntity;
import com.medsavy.medsavyinventorymanagementservice.enums.Transaction;
import com.medsavy.medsavyinventorymanagementservice.exchanges.AddMedRequest;
import com.medsavy.medsavyinventorymanagementservice.exchanges.AddMedResponse;
import com.medsavy.medsavyinventorymanagementservice.exchanges.CreateInventoryResponse;
import com.medsavy.medsavyinventorymanagementservice.exchanges.GetMedResponse;
import com.medsavy.medsavyinventorymanagementservice.exchanges.GetSalesResponse;
import com.medsavy.medsavyinventorymanagementservice.exchanges.IVUpdateRequest;
import com.medsavy.medsavyinventorymanagementservice.exchanges.IVUpdateResponse;
import com.medsavy.medsavyinventorymanagementservice.repository.InventoryRepository;
import com.medsavy.medsavyinventorymanagementservice.repository.SalesRepository;
import com.medsavy.medsavyinventorymanagementservice.repository.UserInventoryRepository;
import com.medsavy.medsavyinventorymanagementservice.repository.UserRepository;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
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

  private final SalesRepository salesRepository;



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
  public GetMedResponse getMedicinesBySearchString(String searchString, Integer inventoryId) {
    List<MedInventoryEntity> medIVEntities = inventoryRepository
        .findMedInventoryEntitiesByInventoryIdAndSearchString(inventoryId, searchString);

    List<Medicine> medicines = structureMedicineList(medIVEntities);

    return new GetMedResponse(medicines);
  }

  @Override
  public GetMedResponse getAllMedicinesByInventoryId(Integer inventoryId) {
    List<MedInventoryEntity> medIVEntities = inventoryRepository
        .findMedInventoryEntitiesByInventoryId(inventoryId);

    List<Medicine> medicineList = structureMedicineList(medIVEntities);

    return new GetMedResponse(medicineList);
  }

  @Override
  public IVUpdateResponse updateMedInInventoryAfterSell(Integer inventoryId, IVUpdateRequest updateRequest) {

    IVUpdateResponse updateResponse = new IVUpdateResponse();
    List<MedInventoryEntity> medIVEntities = inventoryRepository
        .findMedInventoryEntitiesByInventoryIdAndSearchString(
            inventoryId,
            updateRequest.getMedName()
        );

    medIVEntities.sort(new Comparator<MedInventoryEntity>() {
      @Override
      public int compare(MedInventoryEntity o1, MedInventoryEntity o2) {
        return o2.getQuantity() - o1.getQuantity();
      }
    });

    // update all batches or single batch of a medicine based on qty
    Integer totalQty = getTotalQtyForSingleMedIVEntity(medIVEntities);

    int requiredQty = updateRequest.getQuantity();
    List<MedInventoryEntity> possibleUpdatedEntities = new ArrayList<>();
    if(requiredQty <= totalQty) {
     possibleUpdatedEntities = updateMedIVEntitiesQuantity(
         medIVEntities, totalQty, requiredQty
     );

     inventoryRepository.saveAll(possibleUpdatedEntities);
     Integer updatedQuantity = totalQty - requiredQty;
     Integer batchesUpdated = possibleUpdatedEntities.size();
     updateResponse = new IVUpdateResponse(updateRequest.getMedName(), inventoryId,
         updatedQuantity, batchesUpdated);
     updateResponse.setMessage("Inventory Updated successfully");

     Integer sellerId = userInventoryRepository.findById(inventoryId)
         .get()
         .getUserEntity()
         .getUserId();

     createSalesReport(inventoryId,
         updateRequest.getMedName(),
         updateRequest.getCustomerId(),
         updateRequest.getQuantity(),
         sellerId, requiredQty * medIVEntities.get(0).getPrice()
     );
     return updateResponse;
    }
    updateResponse.setMedName(updateRequest.getMedName());
    updateResponse.setInventoryId(updateResponse.getInventoryId());
    updateResponse.setMessage("Update failed due to insufficient quantity");

    return updateResponse;
  }

  @Override
  public GetSalesResponse getSalesDataBySalesId(Integer salesId) {
    Optional<SalesEntity> salesEntity = salesRepository.findById(salesId);
    GetSalesResponse salesResponse = new GetSalesResponse();
    if(salesEntity.isPresent()) {
      List<SalesEntity> salesEntityList = new ArrayList<>();
      salesEntityList.add(salesEntity.get());
      salesResponse = new GetSalesResponse(salesEntityList);
    }

    return salesResponse;
  }

  private void createSalesReport(Integer inventoryId, String medName, Integer customerId,
      Integer quantity, Integer sellerId, double amount) {

    SalesEntity salesEntity = new SalesEntity(
        SELL.name(),
        medName,
        customerId,
        sellerId,
        quantity,
        amount
    );

    salesRepository.save(salesEntity);
  }

  private List<MedInventoryEntity> updateMedIVEntitiesQuantity(List<MedInventoryEntity> medIVEntities, int totalQty,
      int requiredQty) {
    List<MedInventoryEntity> toBeUpdatedMedIVEntities = new ArrayList<>();

    for (MedInventoryEntity medIVEntity : medIVEntities) {
      if (requiredQty != 0 && requiredQty <= medIVEntity.getQuantity()) {
        medIVEntity.decreaseQuantity(requiredQty);
        toBeUpdatedMedIVEntities.add(medIVEntity);
        break;

      } else if (requiredQty != 0 && requiredQty > medIVEntity.getQuantity()) {
        int current = medIVEntity.getQuantity();
        medIVEntity.decreaseQuantity(current);
        requiredQty = requiredQty - current;
        toBeUpdatedMedIVEntities.add(medIVEntity);
      }
    }
    return toBeUpdatedMedIVEntities;
  }

  private Integer getTotalQtyForSingleMedIVEntity(List<MedInventoryEntity> medIVEntities) {
    AtomicReference<Integer> total = new AtomicReference<>(0);
    medIVEntities.forEach(medIVEntity -> total.updateAndGet(v -> v + medIVEntity.getQuantity()));
    return total.get();
  }

  /**
   * Method structures medicines, like each med will have list of batches
   * @param medIVEntities
   * @return
   */
  private List<Medicine> structureMedicineList(List<MedInventoryEntity> medIVEntities) {
    List<Medicine> medicineList = new ArrayList<>();
    Map<String, Medicine> medicineBatch = new HashMap<>();

    medIVEntities.forEach(medIVEntity -> {
      Batch batch = new Batch(medIVEntity.getBatchId(), medIVEntity.getExpiryDate(),
          medIVEntity.getQuantity());
      Medicine medicine = null;
      if(!medicineBatch.containsKey(medIVEntity.getName())) {
        medicine = new Medicine(medIVEntity.getName(), medIVEntity.getType(), medIVEntity.getPrice());
        medicine.addBatch(batch);
        medicine.setTotalQty(batch.getQuantity());
        medicineBatch.put(medicine.getName(), medicine);
        medicineList.add(medicine);
      } else {
        medicine = medicineBatch.get(medIVEntity.getName());
        medicine.addBatch(batch);
        medicine.setTotalQty(medicine.getTotalQty() + batch.getQuantity());
      }
    });


    return medicineList;
  }
}
