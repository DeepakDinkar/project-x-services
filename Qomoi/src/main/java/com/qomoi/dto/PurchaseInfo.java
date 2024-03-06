package com.qomoi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseInfo {

   @NotNull
   @Valid
   public AddressDto address;

   @NotNull
   @Valid
   public List<PurchaseDto> courses;

   @NotNull
   public Boolean saveAddress;

}
