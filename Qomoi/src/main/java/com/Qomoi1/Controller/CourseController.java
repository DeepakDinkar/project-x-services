package com.Qomoi1.Controller;

import com.Qomoi1.Service.CourseService;
import com.Qomoi1.dto.CourseResponse;
import com.Qomoi1.entity.CoursesEntity;
import com.Qomoi1.entity.VerticalEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/get-course/{id}")
    public ResponseEntity<CourseResponse> getCourseId(@PathVariable Long id) {
        Optional<CourseResponse> courseResponse = courseService.getCourseId(id);
        return courseResponse.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/save-course")
    public ResponseEntity<String> saveCourses(@RequestBody CoursesEntity coursesEntity) {
        if (coursesEntity != null) {
            courseService.saveCourse(coursesEntity);
            return new ResponseEntity<>("Course saved successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/course-by-vertical/{slug}")
    public ResponseEntity<List<CourseResponse>> getCourseByTopic(@PathVariable String slug) {

        if (slug != null) {
            List<CourseResponse> response = courseService.getCourseByVerticals(slug);
            if (!response.isEmpty()) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get-top-courses")
    public ResponseEntity<List<Map<String,Object>>> getTopCoursesByVertical() {
       List<Map<String,Object>> courseByTopic= courseService.findTopCoursesByVerticals();
        return new ResponseEntity<>(courseByTopic, HttpStatus.OK);
    }

    @GetMapping("/explore/{page}")
    public ResponseEntity<Page<CoursesEntity>> exploreCourse(@PathVariable int page) {
        int pageSize = 20;
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
        Page<CoursesEntity> pageCourse = courseService.getAllCourse(pageRequest);

        return new ResponseEntity<>(pageCourse, HttpStatus.OK);
    }
}