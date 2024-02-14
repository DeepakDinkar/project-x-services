package com.qomoi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoogleResponse {

    private String token;
    private String user;
    private String additionalInfo;

}
