package com.Qomoi.Qomoi.ServiceImpl;

import com.Qomoi.Qomoi.Entity.UserEntity;
import com.Qomoi.Qomoi.Enum.Role;
import com.Qomoi.Qomoi.Repository.UserRepository;
import com.Qomoi.Qomoi.Request.SignupRequest;
import com.Qomoi.Qomoi.Service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserEntity signupUser(SignupRequest signupRequest){
      UserEntity userEntity = new UserEntity();
      userEntity.setFirstName(signupRequest.getFirstName());
      userEntity.setLastName(signupRequest.getLastName());
      userEntity.setEmail(signupRequest.getEmail());
      userEntity.setRole(Role.USER);
      userEntity.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
      return userRepository.save(userEntity);

    }


    public UserEntity signupAdmin(SignupRequest signupRequest){
        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(signupRequest.getFirstName());
        userEntity.setLastName(signupRequest.getLastName());
        userEntity.setEmail(signupRequest.getEmail());
        userEntity.setRole(Role.ADMIN);
        userEntity.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        return userRepository.save(userEntity);

    }


}
