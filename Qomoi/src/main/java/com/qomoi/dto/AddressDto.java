package com.qomoi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {

    private String addressName;
    private String address1;
    private String address2;
    private String country;
    private String city;
    private String state;
    private String zipcode;

}
