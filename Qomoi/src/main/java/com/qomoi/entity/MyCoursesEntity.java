package com.qomoi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "MyCourse")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyCoursesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "myCourseId" )
    @SequenceGenerator(name = "myCourseId",sequenceName = "myCourseId",allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "all_courses")
    private List<String> allCourses;

}
