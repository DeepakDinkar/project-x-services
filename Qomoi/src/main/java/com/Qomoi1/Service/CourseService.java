package com.Qomoi1.Service;

import com.Qomoi1.Entity.CoursesEntity;
import com.Qomoi1.Entity.VerticalEntity;
import com.Qomoi1.Response.CourseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CourseService {


     Page<CoursesEntity> getAllCourse(PageRequest pageRequest);
     Optional<CourseResponse> getCourseId(Long id);

     void saveCourse(CoursesEntity coursesEntity);

     List<CourseResponse> getCourseByVerticals(String slug);

     Map<VerticalEntity, List<CoursesEntity>> findTopCoursesByVerticals();

}
