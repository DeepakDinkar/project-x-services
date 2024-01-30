package com.qomoi.controller;


import com.qomoi.entity.VerticalCoursesEntity;
import com.qomoi.service.VerticalService;
import com.qomoi.entity.VerticalEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("verticals")
public class VerticalController {

    private final VerticalService verticalService;

    public VerticalController(VerticalService verticalService) {
        this.verticalService = verticalService;
    }

    @GetMapping
    public ResponseEntity<List<VerticalEntity>> getAllTopics() {
        List<VerticalEntity> allTopics = verticalService.getVerticals();
        return ResponseEntity.ok(allTopics);
    }

    @PostMapping("/save-vertical")
    public ResponseEntity<String> saveTopic(@RequestBody VerticalEntity verticalEntity) {
        if (verticalEntity != null) {
            verticalService.saveTopic(verticalEntity);
            return ResponseEntity.ok("Course saved successfully");
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{query}")
    public ResponseEntity<List<VerticalEntity>> searchVerticals(@PathVariable String query) {
        if (query != null) {
            List<VerticalEntity> response = verticalService.searchVerticals(query);
            return ResponseEntity.ok(response);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{slug}")
    public ResponseEntity<VerticalCoursesEntity> getVerticalCoursesBySlug(@PathVariable String slug) {

        VerticalCoursesEntity verticalCoursesEntity = verticalService.getVerticalCoursesBySlug(slug);
        return new ResponseEntity<>(verticalCoursesEntity, HttpStatus.OK);
    }

}
