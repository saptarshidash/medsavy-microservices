package com.medsavy.medsavyuserservice.services;

import com.medsavy.medsavyuserservice.Entity.CartItemEntity;
import com.medsavy.medsavyuserservice.Entity.SubscriptionEntity;
import com.medsavy.medsavyuserservice.dtos.CartItem;
import com.medsavy.medsavyuserservice.exchanges.AddCartRequest;
import com.medsavy.medsavyuserservice.exchanges.AddCartResponse;
import com.medsavy.medsavyuserservice.exchanges.DeleteCartResponse;
import com.medsavy.medsavyuserservice.exchanges.GetCartResponse;
import com.medsavy.medsavyuserservice.exchanges.TakeSubscriptionReq;
import com.medsavy.medsavyuserservice.exchanges.TakeSubscriptionResponse;
import com.medsavy.medsavyuserservice.repository.CartRepository;
import com.medsavy.medsavyuserservice.repository.SubscriptionRepository;
import java.time.LocalDate;
import java.util.List;
import net.bytebuddy.asm.Advice.Local;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

  @Autowired
  private CartRepository cartRepository;

  @Autowired
  private SubscriptionRepository subscriptionRepository;

  private ModelMapper mapper = new ModelMapper();

  @Override
  public AddCartResponse addMedIntoCart(AddCartRequest request) {
    AddCartResponse cartResponse = new AddCartResponse();
    CartItemEntity cartEntity = CartItemEntity.builder().inventoryId(request.getInventoryId())
            .medName(request.getMedName())
                .customerId(request.getCustomerId())
                    .quantity(request.getQuantity())
                        .totalPrice(request.getTotalPrice()).build();

    cartRepository.save(cartEntity);

    if(cartEntity.getId() != null) {
      cartResponse.setMedName(cartEntity.getMedName());
      cartResponse.setMessage("Medicine added to cart");
      cartResponse.setSuccess(true);
      return cartResponse;
    }
    cartResponse.setMessage("Medicine can not be added");
    cartResponse.setSuccess(false);
    return cartResponse;
  }

  @Override
  public GetCartResponse getCartByCustomerId(Integer customerId) {
    List<CartItemEntity> cartEntityList = cartRepository.getCartEntitiesByCustomerId(customerId);

    List<CartItem> cartItemList = cartEntityList.stream()
        .map(entity -> mapper.map(entity, CartItem.class))
        .toList();
    return new GetCartResponse(cartItemList);
  }

  @Override
  public DeleteCartResponse deleteCartItemsByCustomerId(Integer customerId) {
    DeleteCartResponse response = new DeleteCartResponse();
    Integer deletedRows = cartRepository.deleteCartItemsByCustomerId(customerId);

    if(deletedRows > 0) {
      response.setDeletedRows(deletedRows);
      response.setSuccess(true);
      return response;
    }
    response.setSuccess(false);
    response.setDeletedRows(0);
    return response;
  }

  @Override
  public TakeSubscriptionResponse takeSubscription(TakeSubscriptionReq request) {
    TakeSubscriptionResponse response = new TakeSubscriptionResponse();

    SubscriptionEntity subsEntity = subscriptionRepository
        .findByCustomerIdAndMedName(request.getCustomerId(), request.getMedName());

    if(subsEntity != null) {
      LocalDate today = LocalDate.now();
      LocalDate reminder = today.plusMonths(request.getRemindAfterMonth());
      subsEntity.setDate(reminder);
      subsEntity.setQuantity(request.getQuantity());
      subscriptionRepository.save(subsEntity);
    } else {
      subsEntity = mapper.map(request, SubscriptionEntity.class);
      LocalDate today = LocalDate.now();
      LocalDate reminder = today.plusMonths(request.getRemindAfterMonth());
      subsEntity.setDate(reminder);
      subscriptionRepository.save(subsEntity);
    }


    if(subsEntity.getSubsid() != null) {
      response.setMessage("Subscribed");
      response.setSuccess(true);
      return response;
    }
    response.setSuccess(false);
    return response;
  }


}
