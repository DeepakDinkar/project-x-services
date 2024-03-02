package com.qomoi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@Table(name = "cart_data")
@NoArgsConstructor
@AllArgsConstructor
public class AddToCart {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "add_to_cart_s")
    @SequenceGenerator(name = "add_to_cart_s", sequenceName = "add_to_cart_s", allocationSize = 1, initialValue = 10000)
    @Column(name = "id")
    private Long id;

    @Column(name = "json", columnDefinition = "text")
    private String json;

    @Column(name = "secret_key")
    private String secretKey;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @PrePersist
    public void prePersist() {
        if (createdDate == null) {
            createdDate = LocalDate.now();
        }
    }







}
