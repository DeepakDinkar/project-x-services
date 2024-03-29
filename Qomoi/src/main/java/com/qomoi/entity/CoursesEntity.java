package com.qomoi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "courses")
@NoArgsConstructor
@AllArgsConstructor
public class CoursesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "courseId")
    @SequenceGenerator(name = "courseId", sequenceName = "courseId", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "slug", nullable = false)
    private String slug;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "campaign_template_rating")
    private String campaignTemplateRating;

    @Column(name = "campaign_template_courseName")
    private String campaignTemplateCourseName;

    @Column(name = "course_content", length = 1000)
    private String courseContent;

    @Column(name = "key_take_away", length = 500)
    private List<String> keyTakeAway;

    @Column(name = "is_trending")
    private Boolean isTrending;

    @Column(name = "course_added_date")
    private Date courseAddedDate;

    @Column(name = "trainer_id")
    private List<Long> trainerId;

    @Column(name = "course_amt")
    private Double courseAmt;

}
