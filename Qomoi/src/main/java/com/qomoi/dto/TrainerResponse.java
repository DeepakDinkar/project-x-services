package com.qomoi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainerResponse {

    private List<String> courseName;
    private String trainerName;
    private String phoneNumber;
    private String imageUrl;
    private String email;

}
