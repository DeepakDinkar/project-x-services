package com.qomoi1.Controller;


import com.qomoi1.Service.VerticalService;
import com.qomoi1.entity.VerticalEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("verticals")
public class VerticalController {

    @Autowired
    VerticalService verticalService;

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

}