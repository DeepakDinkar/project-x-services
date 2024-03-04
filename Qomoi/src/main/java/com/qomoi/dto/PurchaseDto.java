package com.qomoi.dto;

import jakarta.persistence.Column;
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

    private Double courseAmt;

    private String transactionId;

    private String slug;

    private String imageUrl;
//
//    private String address1;
//
//    private String address2;
//
//    private String country;
//
//    private String city;
//
//    private String state;
//
//    private String zipcode;
//
//    private Boolean isFutureUse;

}
