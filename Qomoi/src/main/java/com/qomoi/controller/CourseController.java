package com.qomoi.controller;

import com.qomoi.dto.CourseLocationResponse;
import com.qomoi.service.CourseService;
import com.qomoi.dto.CourseResponse;
import com.qomoi.entity.CoursesEntity;
import com.qomoi.entity.VerticalCoursesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseLocationResponse> getCourseDetails(@PathVariable Long courseId) {
        Optional<CourseLocationResponse> courseResponse = courseService.getCourseId(courseId);
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

    @GetMapping("/verticals/{slug}")
    public ResponseEntity<List<CoursesEntity>> getCourseByTopic(@PathVariable String slug) {
        if (slug != null) {
            List<CoursesEntity> response = courseService.getAllCoursesByVerticalSlug(slug);
            if (!response.isEmpty()) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/trending")
    public ResponseEntity<List<VerticalCoursesEntity>> getTrendingVerticalCourses() {
        List<VerticalCoursesEntity> courseByTopic= courseService.getTrendingVerticalCourses();
        return new ResponseEntity<>(courseByTopic, HttpStatus.OK);
    }

    @GetMapping("/explore/{page}")
    public ResponseEntity<List<CourseLocationResponse>> exploreCourse(@PathVariable int page) {
        int pageSize = 25;
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
        List<CourseLocationResponse> pageCourse = courseService.getAllCourse(pageRequest);

        return new ResponseEntity<>(pageCourse, HttpStatus.OK);
    }

//    @GetMapping("/explore/{page}")
//    public ResponseEntity<Page<CourseLocationResponse>> exploreCourse(@PathVariable int page) {
//        int pageSize = 25;
//        PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
//        Page<CourseLocationResponse> pageCourse = courseService.getAllCourse(pageRequest);
//
//        return new ResponseEntity<>(pageCourse, HttpStatus.OK);
//    }
}