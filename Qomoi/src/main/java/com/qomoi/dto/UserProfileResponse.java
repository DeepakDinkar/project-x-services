package com.qomoi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private String firstName;
    private String LastName;
    private String mobile;
    private String email;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String zipcode;

}