package com.qomoi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "billing_address")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class BillingAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "billing_address_s")
    @SequenceGenerator(name = "billing_address_s", sequenceName = "billing_address_s", initialValue = 10000)
    private Long id;

    @Column(name = "json_data", columnDefinition = "TEXT")
    private String jsonData;

    @Column(name = "user_id")
    private Long userK;

}
