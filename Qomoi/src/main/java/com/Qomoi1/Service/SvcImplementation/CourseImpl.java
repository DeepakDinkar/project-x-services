package com.Qomoi1.Service.SvcImplementation;

import com.Qomoi1.Entity.CoursesEntity;
import com.Qomoi1.Entity.VerticalEntity;
import com.Qomoi1.Repository.CourseRepository;
import com.Qomoi1.Response.CourseResponse;
import com.Qomoi1.Service.CourseService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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


    public Map<VerticalEntity, List<CoursesEntity>> getTopCoursesByTopic(){

        List<Object[]> result = courseRepository.findTopCoursesByTopic();
        Map<VerticalEntity, List<CoursesEntity>> topCoursesMap = new LinkedHashMap<>();
        for (Object[] row : result ){
            VerticalEntity vertical = (VerticalEntity) row[0];
            CoursesEntity course = (CoursesEntity) row[1];

            topCoursesMap.computeIfAbsent(vertical, k -> new ArrayList<>());
            List<CoursesEntity> courses = topCoursesMap.get(vertical);
            courses.add(course);

            if(topCoursesMap.size() == 3 && courses.size() == 2){
                break;
            }
        }
        return topCoursesMap;
    }
}
