package com.qomoi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalSearchEntity {

    private List<VerticalEntity> verticals = new ArrayList<>();
    private List<CoursesEntity> courses = new ArrayList<>();
}
