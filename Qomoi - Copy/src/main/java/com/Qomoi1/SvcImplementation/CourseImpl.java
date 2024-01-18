package com.Qomoi1.SvcImplementation;

import com.Qomoi1.Entity.CoursesEntity;
import com.Qomoi1.Repository.CourseRepository;
import com.Qomoi1.Response.CourseResponse;
import com.Qomoi1.Service.CourseService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional

public class CourseImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;



    @Override
    public  Optional<CourseResponse> getCourseId(Long id) {

        Optional<CoursesEntity> courseRes = courseRepository.findById(id);
        return courseRes.map(CourseResponse::new);

    }

    public void saveCourse(CoursesEntity coursesEntity) {

        courseRepository.save(coursesEntity);

    }

    public List<CourseResponse> getCourseByTopic(Long id) {
        List<CourseResponse> courseList = courseRepository.getCourseByTopic(id);
        return courseList != null ? courseList : Collections.emptyList();
    }
}
