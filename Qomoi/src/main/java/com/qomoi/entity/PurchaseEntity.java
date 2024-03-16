package com.qomoi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "purchase")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "purchaseId")
    @SequenceGenerator(name = "purchaseId", sequenceName = "purchaseId", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "course_name")
    private String courseName;

    @Column(name = "email")
    private String email;

    @Column(name = "location")
    private String location;

    @Column(name = "course_date")
    private Date courseDate;

    @Column(name = "course_amt")
    private Double courseAmt;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "purchase_date")
    private Date purchaseDate;

    @Column(name = "slug")
    private String slug;

    @Column(name = "status")
    private String status = "P";

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "address1")
    private String address1;

    @Column(name = "address2", nullable = true)
    private String address2;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "zipcode")
    private String zipcode;

    @Column(name = "is_future_use")
    private Boolean isFutureUse;

}
