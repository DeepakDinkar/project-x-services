package com.qomoi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/qomoi")
public class QomoiController {

    @PostMapping("/callback")
    public ResponseEntity<?> callBack() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
