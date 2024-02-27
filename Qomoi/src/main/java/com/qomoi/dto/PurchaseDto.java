package com.qomoi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseDto {

    private Long courseId;

    private String location;

    private Date courseDate;

    private float courseAmt;

    private String transactionId;

    private String slug;

}
