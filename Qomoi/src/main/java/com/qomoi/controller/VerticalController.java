package com.qomoi.controller;


import com.qomoi.entity.VerticalCoursesEntity;
import com.qomoi.entity.VerticalEntity;
import com.qomoi.service.VerticalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/verticals")
public class VerticalController {

    private final VerticalService verticalService;

    public VerticalController(VerticalService verticalService) {
        this.verticalService = verticalService;
    }

    @GetMapping
    public ResponseEntity<List<VerticalEntity>> getAllTopics() {
        try {
            List<VerticalEntity> allTopics = verticalService.getVerticals();
            return ResponseEntity.ok(allTopics);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/save-vertical")
    public ResponseEntity<String> saveTopic(@RequestBody VerticalEntity verticalEntity) {
        try {
            if (verticalEntity != null) {
                verticalService.saveTopic(verticalEntity);
                return ResponseEntity.ok("Course saved successfully");
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{slug}")
    public ResponseEntity<VerticalCoursesEntity> getVerticalCoursesBySlug(@PathVariable String slug) {
        try {
            VerticalCoursesEntity verticalCoursesEntity = verticalService.getVerticalCoursesBySlug(slug);
            return ResponseEntity.ok(verticalCoursesEntity);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
