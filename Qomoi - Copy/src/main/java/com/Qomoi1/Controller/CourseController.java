package com.Qomoi1.Controller;

import com.Qomoi1.Entity.CoursesEntity;
import com.Qomoi1.Response.CourseResponse;
import com.Qomoi1.Service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/get-course")
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
}