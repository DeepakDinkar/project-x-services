package com.qomoi.dto;

import com.qomoi.entity.CoursesEntity;
import com.qomoi.entity.VerticalEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExploreSearchDto {

    private List<VerticalEntity> verticals = new ArrayList<>();
    private List<CoursesEntity> courses = new ArrayList<>();
}
