package com.qomoi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto {

    private String firstName;
    private String lastName;
    private String phoneNo;
    private String email;
    private String imageUrl;
    private String address1;
    private String address2;
    private String country;
    private String city;
    private String zipCode;
}
