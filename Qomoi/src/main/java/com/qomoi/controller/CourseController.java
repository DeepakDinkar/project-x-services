package com.qomoi.controller;

import com.qomoi.dto.CourseLocationResponse;
import com.qomoi.dto.TrainerResponse;
import com.qomoi.entity.CourseVerticalEntity;
import com.qomoi.entity.CoursesEntity;
import com.qomoi.service.CourseService;
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
        try {
            if (courseId == null || courseId <= 0) {
                return ResponseEntity.badRequest().build();
            }
            Optional<CourseLocationResponse> courseResponse = courseService.getCourseId(courseId);
            return courseResponse.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/save-course")
    public ResponseEntity<String> saveCourses(@RequestBody CoursesEntity coursesEntity) {
        try {
            if (coursesEntity == null) {
                return ResponseEntity.badRequest().build();
            }
            courseService.saveCourse(coursesEntity);
            return new ResponseEntity<>("Course saved successfully", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/verticals/{slug}")
    public ResponseEntity<List<CoursesEntity>> getCourseByTopic(@PathVariable String slug) {
        try {
            if (slug == null || slug.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            List<CoursesEntity> response = courseService.getAllCoursesByVerticalSlug(slug);
            if (!response.isEmpty()) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/banner")
    public ResponseEntity<List<CourseVerticalEntity>> getTrendingCourses() {
        try {
            List<CourseVerticalEntity> trendingCourse = courseService.getTrendingVerticalCourses();
            return new ResponseEntity<>(trendingCourse, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/trending/{page}")
    public ResponseEntity<Page<CoursesEntity>> getTrending(@PathVariable int page) {
        try {
            if (page <= 0) {
                return ResponseEntity.badRequest().build();
            }
            int pageSize = 25;
            PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
            Page<CoursesEntity> trendingCourse = courseService.getAllTrending(pageRequest);
            return new ResponseEntity<>(trendingCourse, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/recommended/{page}")
    public ResponseEntity<Page<CoursesEntity>> getRecommendedCourses(@PathVariable int page) {
        try {
            if (page <= 0) {
                return ResponseEntity.badRequest().build();
            }
            int pageSize = 25;
            PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
            Page<CoursesEntity> recommendedCourses = courseService.getRecommendedCourses(pageRequest);
            return new ResponseEntity<>(recommendedCourses, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/similar/{slug}/{page}")
    public ResponseEntity<Page<CoursesEntity>> getSimilarCourses(@PathVariable int page, @PathVariable String slug) {
        try {
            if (page <= 0 || slug == null || slug.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            int pageSize = 25;
            PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
            Page<CoursesEntity> similarCourses = courseService.getSimilarCourses(pageRequest, slug);
            return new ResponseEntity<>(similarCourses, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/explore/{page}")
    public ResponseEntity<List<CourseLocationResponse>> exploreCourse(@PathVariable int page) {
        try {
            if (page <= 0) {
                return ResponseEntity.badRequest().build();
            }
            int pageSize = 25;
            PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
            List<CourseLocationResponse> pageCourse = courseService.getAllCourse(pageRequest);
            return new ResponseEntity<>(pageCourse, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/locations")
    public ResponseEntity<List<?>> getAllLocation() {
        try {
            List<?> locations = courseService.getAllLocation();
            return new ResponseEntity<>(locations, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/trainers/{page}")
    public ResponseEntity<Page<TrainerResponse>> getTrainers(@PathVariable int page) {
        try {
            if (page <= 0) {
                return ResponseEntity.badRequest().build();
            }
            int pageSize = 25;
            PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
            Page<TrainerResponse> pageTrainer = courseService.getAllTrainers(pageRequest);
            return new ResponseEntity<>(pageTrainer, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}