package com.qomoi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavedResponseDto {

    private SignupResponseDto savedRecord;

    private ResponseDto response;
}
