package com.Qomoi1.Service;


import com.Qomoi1.dto.CourseResponse;
import com.Qomoi1.entity.CoursesEntity;
import com.Qomoi1.entity.TrendingVerticalEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

public interface CourseService {

    Page<CoursesEntity> getAllCourse(PageRequest pageRequest);
    Optional<CourseResponse> getCourseId(Long id);
    void saveCourse(CoursesEntity coursesEntity);
    List<CoursesEntity> getAllCoursesByVerticalSlug(String slug);
    List<TrendingVerticalEntity> getTrendingVerticalCourses();
}
