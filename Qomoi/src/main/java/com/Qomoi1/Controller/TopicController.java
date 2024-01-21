package com.Qomoi1.Controller;

import com.Qomoi1.Entity.TopicEntity;
import com.Qomoi1.Repository.TopicRepository;
import com.Qomoi1.Service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/topic")
public class TopicController {

    @Autowired
    TopicRepository topicRepository;

    @Autowired
    TopicService topicService;

    @GetMapping("/get-topic")
    public ResponseEntity<List<TopicEntity>> getAllTopics(){
       List<TopicEntity> allTopics =  topicService.getAllTopics();
        return ResponseEntity.ok(allTopics);
    }

    @PostMapping("/save-topic")

    public ResponseEntity<String> saveTopic(@RequestBody TopicEntity topicEntity ){
        topicService.saveTopic(topicEntity);
        return ResponseEntity.ok("Course saved successfully");
    }

}
