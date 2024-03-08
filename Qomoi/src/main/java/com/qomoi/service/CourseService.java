package com.qomoi.service;


import com.qomoi.dto.CourseLocationResponse;
import com.qomoi.dto.CourseResponse;
import com.qomoi.dto.LocationResponse;
import com.qomoi.dto.TrainerResponse;
import com.qomoi.entity.CourseVerticalEntity;
import com.qomoi.entity.CoursesEntity;
import com.qomoi.entity.VerticalCoursesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

public interface CourseService {

    List<CourseLocationResponse> getAllCourse(PageRequest pageRequest);

    Optional<CourseLocationResponse> getCourseId(Long id);
    void saveCourse(CoursesEntity coursesEntity);
    List<CoursesEntity> getAllCoursesByVerticalSlug(String slug);
    List<CourseVerticalEntity> getTrendingVerticalCourses();

    Page<CoursesEntity> getAllTrending(PageRequest pageRequest);

    Page<CoursesEntity> getRecommendedCourses(PageRequest pageRequest);
    Page<CoursesEntity> getSimilarCourses(PageRequest pageRequest, String slug);
    List<?> getAllLocation();
    Page<TrainerResponse> getAllTrainers(PageRequest pageRequest);
}
