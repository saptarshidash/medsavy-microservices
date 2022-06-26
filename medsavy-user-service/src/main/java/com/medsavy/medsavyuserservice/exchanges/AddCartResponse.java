package com.medsavy.medsavyuserservice.exchanges;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCartResponse {

  private String medName;
  private String message;
  private Boolean success;
}
