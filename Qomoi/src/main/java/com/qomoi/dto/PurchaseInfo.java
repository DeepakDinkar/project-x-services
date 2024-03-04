package com.qomoi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseInfo {

   public AddressDto address;
   public List<PurchaseDto> courses;
   public Boolean saveAddress;

}
