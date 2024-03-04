package com.qomoi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallBackDto {

    private String firstName;
    private String lastName;
    private String mobile;
    private String email;
    private Boolean groupDiscount;
    private String conversation;
    private PhoneOrEmail phoneOrEmail;

}
