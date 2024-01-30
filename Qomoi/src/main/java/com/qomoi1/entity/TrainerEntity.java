package com.qomoi1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "trainers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "id")
    @SequenceGenerator(name = "id",sequenceName = "id",allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "trainer_name")
    private String trainerName;

    @Column(name = "slug")
    private String slug;
}
