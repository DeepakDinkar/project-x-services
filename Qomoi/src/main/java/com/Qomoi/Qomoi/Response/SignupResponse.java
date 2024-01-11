package com.Qomoi.Qomoi.Response;

import com.Qomoi.Qomoi.Entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupResponse {

    private String message;

    private String email;

}
