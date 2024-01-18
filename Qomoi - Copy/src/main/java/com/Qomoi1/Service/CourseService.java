package com.Qomoi1.Service;

import com.Qomoi1.Entity.CoursesEntity;
import com.Qomoi1.Response.CourseResponse;

import java.util.Optional;

public interface CourseService {


    Optional<CourseResponse> getCourseId(Long id);

     void saveCourse(CoursesEntity coursesEntity);


}
