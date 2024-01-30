package com.qomoi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrendingVerticalEntity {

    private String slug;
    private String title;
    private String imageUrl;
    private List<CoursesEntity> courses;
}
