package com.qomoi.dto;

import com.qomoi.entity.CoursesEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseResponse {

    private String coursesName;
    private String location;
    private Date courseDate;
    private Double courseAmt;
    private String transactionId;
    private Date purchasedDate;
    private String slug;
    private String imageUrl;

}