package com.Qomoi1.Controller;

import com.Qomoi1.Request.AddressDto;
import com.Qomoi1.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/save-address/id")
    public ResponseEntity<String> saveAddress(@RequestBody AddressDto addressDto, @PathVariable Long id){
        userService.saveAddress(addressDto, id);
        return ResponseEntity.ok("Address saved Successfully");
    }

}
