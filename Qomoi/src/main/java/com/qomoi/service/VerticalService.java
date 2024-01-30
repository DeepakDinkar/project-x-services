package com.qomoi.service;



import com.qomoi.entity.VerticalCoursesEntity;
import com.qomoi.entity.VerticalEntity;

import java.util.List;

public interface VerticalService {

    List<VerticalEntity> getVerticals();
    void saveTopic(VerticalEntity verticalEntity);
    List<VerticalEntity> searchVerticals(String query);
    VerticalCoursesEntity getVerticalCoursesBySlug(String slug);
}
