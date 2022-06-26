package com.medsavy.authservice.configs;

import com.medsavy.authservice.calls.UserDto;
import org.springframework.stereotype.Service;

import java.util.Optional;
import org.springframework.web.client.RestTemplate;

@Service
public class MyUserDetailService  {


  private RestTemplate restTemplate = new RestTemplate();

  public UserDto fetchUserData(String username) {
    return restTemplate.getForObject(
        "http://localhost:8080/api/v1/user/customer/role?username={username}",
        UserDto.class,
        username
    );
  }



}

