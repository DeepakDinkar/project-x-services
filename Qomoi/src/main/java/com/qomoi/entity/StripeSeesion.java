package com.qomoi.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stripe_session")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StripeSeesion {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stripe_session_s")
    @SequenceGenerator(name = "stripe_session_s", sequenceName = "stripe_session_s", initialValue = 10000)
    private Long id;

    @Column(name = "json_data", columnDefinition = "TEXT")
    private String jsonData;
}
