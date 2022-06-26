package com.medsavy.medsavyuserservice.services;

import com.medsavy.medsavyuserservice.Entity.UserEntity;
import com.medsavy.medsavyuserservice.exchanges.UserRegistrationRequest;
import com.medsavy.medsavyuserservice.exchanges.UserRegistrationResponse;
import com.medsavy.medsavyuserservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailService {


  @Autowired
  private UserRepository userRepository;

  public UserRegistrationResponse createUser(UserRegistrationRequest request){

    String message = "";


    if(userRepository.existsByUsername(request.getUsername())){
      message = message + "Username already exists";
    }

    if(!message.isEmpty()){
      UserRegistrationResponse response = new UserRegistrationResponse(
          request.getUsername(),
          request.getRoles(),
          message
      );
      return response;
    }

    UserEntity userEntity = UserEntity.builder().username(request.getUsername())
            .password(request.getPassword())
                .roles(request.getRoles())
                    .build();
    userRepository.save(userEntity);
    UserRegistrationResponse response = new UserRegistrationResponse();
    if(userEntity != null) {
      response.setUsername(userEntity.getUsername());
      response.setRoles(userEntity.getRoles());
      response.setMessage("Registration Successful");
      return response;
    }

    response.setMessage("Registration failed");
    return response;
  }
}
