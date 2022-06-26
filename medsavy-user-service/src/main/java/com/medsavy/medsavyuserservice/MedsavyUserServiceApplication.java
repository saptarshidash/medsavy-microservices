package com.medsavy.medsavyuserservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class MedsavyUserServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(MedsavyUserServiceApplication.class, args);
  }


}
