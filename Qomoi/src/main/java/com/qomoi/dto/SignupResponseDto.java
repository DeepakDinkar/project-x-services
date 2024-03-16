package com.qomoi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupResponseDto {
    private String firstName;
    private String LastName;
    private String mobile;
    private String email;
    private Boolean isFacebook;
    private Boolean isGoogle;
    private Boolean isNormal;
}
