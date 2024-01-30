package com.qomoi.service;


import com.qomoi.dto.CourseResponse;
import com.qomoi.entity.CoursesEntity;
import com.qomoi.entity.VerticalCoursesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

public interface CourseService {

    Page<CoursesEntity> getAllCourse(PageRequest pageRequest);
    Optional<CourseResponse> getCourseId(Long id);
    void saveCourse(CoursesEntity coursesEntity);
    List<CoursesEntity> getAllCoursesByVerticalSlug(String slug);
    List<VerticalCoursesEntity> getTrendingVerticalCourses();
}
