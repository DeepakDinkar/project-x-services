package com.qomoi.controller;

import com.qomoi.dto.CallBackDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/qomoi")
public class QomoiController {

    @PostMapping("/callback")
    public ResponseEntity<?> callBack(
            @RequestBody CallBackDto callBackDto
            ) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
