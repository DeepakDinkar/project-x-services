package com.Qomoi.Qomoi.Controller;


import com.Qomoi.Qomoi.Entity.UserEntity;
import com.Qomoi.Qomoi.Enum.Role;
import com.Qomoi.Qomoi.Repository.UserRepository;
import com.Qomoi.Qomoi.Request.SignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

}
