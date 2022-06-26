package com.medsavy.medsavyuserservice.Controller;

import com.medsavy.medsavyuserservice.exchanges.AddCartRequest;
import com.medsavy.medsavyuserservice.exchanges.AddCartResponse;
import com.medsavy.medsavyuserservice.exchanges.DeleteCartRequest;
import com.medsavy.medsavyuserservice.exchanges.DeleteCartResponse;
import com.medsavy.medsavyuserservice.exchanges.GetCartResponse;
import com.medsavy.medsavyuserservice.exchanges.TakeSubscriptionReq;
import com.medsavy.medsavyuserservice.exchanges.TakeSubscriptionResponse;
import com.medsavy.medsavyuserservice.exchanges.UserRegistrationRequest;
import com.medsavy.medsavyuserservice.exchanges.UserRegistrationResponse;
import com.medsavy.medsavyuserservice.services.MyUserDetailService;
import com.medsavy.medsavyuserservice.services.UserService;
import com.medsavy.medsavyuserservice.services.UserServiceImpl;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserServiceController {

  @Autowired
  private UserService userService;

  @Autowired
  private MyUserDetailService userDetailService;

  @PostMapping("/register")
  public UserRegistrationResponse registerUser(@RequestBody UserRegistrationRequest request) {
    return userDetailService.createUser(request);
  }

  @PostMapping("/customer/cart")
  public AddCartResponse addMedicineInCart(@RequestBody  AddCartRequest request) {
    return userService.addMedIntoCart(request);
  }

  @GetMapping("/customer/cart/items")
  public GetCartResponse getCartItems(@RequestParam Integer customerId){
    return userService.getCartByCustomerId(customerId);
  }

  @DeleteMapping("/customer-cart/items/")
  public DeleteCartResponse clearCartByUserId(@RequestBody DeleteCartRequest request) {
    return userService.deleteCartItemsByCustomerId(request.getCustomerId());
  }

  @PostMapping("/customer/subscription")
  public TakeSubscriptionResponse takeSubscription(@RequestBody TakeSubscriptionReq req) {
    return userService.takeSubscription(req);
  }
}
