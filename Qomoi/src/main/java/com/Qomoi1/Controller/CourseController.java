package com.Qomoi1.Controller;

import com.Qomoi1.Entity.CoursesEntity;
import com.Qomoi1.Entity.VerticalEntity;
import com.Qomoi1.Response.CourseResponse;
import com.Qomoi1.Service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private  CourseService courseService;



    @GetMapping("/get-course/{id}")
    public ResponseEntity<CourseResponse> getCourseId(@PathVariable Long id){
        Optional<CourseResponse>  courseResponse =  courseService.getCourseId(id);
        return courseResponse.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());

    }
    @PostMapping("/save-course")
    public ResponseEntity<String> saveCourses(@RequestBody CoursesEntity coursesEntity) {
        courseService.saveCourse(coursesEntity);
        return ResponseEntity.ok("Course saved successfully");
    }

    @GetMapping("/course-by-vertical/{id}")
    public ResponseEntity<List<CourseResponse>> getCourseByTopic(@PathVariable Long id){

       List<CourseResponse> response =  courseService.getCourseByTopic(id);
        if (!response.isEmpty()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/get-top-courses")
    public ResponseEntity<Map<VerticalEntity,List<CoursesEntity>>> getTopCoursesbyTopic(){
        Map<VerticalEntity,List<CoursesEntity>> courseByTopic = courseService.getTopCoursesByTopic();
        return ResponseEntity.ok(courseByTopic);
    }
}