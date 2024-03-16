package com.qomoi.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "verticals")
@Data
@NoArgsConstructor
public class VerticalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id")
    @SequenceGenerator(name = "id", sequenceName = "id", allocationSize = 1)
    @Column(name = "id")
    private Long id;
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "slug", nullable = false, unique = true)
    private String slug;
    @Column(name = "title")
    private String title;
    private Integer noOfCourses;

    public VerticalEntity(Long id, String slug, String title, String imageUrl, Integer noOfCourses) {
        this.id = id;
        this.slug = slug;
        this.title = title;
        this.imageUrl = imageUrl;
        this.noOfCourses = noOfCourses;
    }


}
