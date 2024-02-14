package com.qomoi.dto;

import com.qomoi.entity.CoursesEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseDto {

    private Long courseId;

    private String location;

    private Date courseDate;

    private String courseAmt;

    private String transactionId;

}
