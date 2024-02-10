package com.qomoi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainerResponse {

    private Long courseId;
    private String trainerName;
    private String phoneNumber;
    private String imageUrl;

}
