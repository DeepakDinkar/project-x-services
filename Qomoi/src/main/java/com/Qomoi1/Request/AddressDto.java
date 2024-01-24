package com.Qomoi1.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {

    private String address1;
    private String address2;
    private String city;
    private String state;
    private Integer zipcode;

}
