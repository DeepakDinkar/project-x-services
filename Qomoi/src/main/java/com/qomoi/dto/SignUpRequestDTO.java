package com.qomoi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
public class SignUpRequestDTO {

    private String lastName;
    private String firstName;
    private String email;
    private String mobile;
    private String country;
    private String street;
    private String city;
    private String state;
    private String pincode;
    private String userType;
    private String password;

}