package com.Qomoi1.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "verticals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerticalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id" )
    @SequenceGenerator(name = "id",sequenceName = "id",allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "slug")
    private String slug;

    @Column(name = "title")
    private String title;

    @Column(name = "image_url")
    private String imageUrl;

}
