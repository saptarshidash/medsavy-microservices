package com.medsavy.authservice.calls;

import com.medsavy.authservice.configs.UserEntity;
import javax.ws.rs.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UserDetails {

  private RestTemplate restTemplate = new RestTemplate();


}
