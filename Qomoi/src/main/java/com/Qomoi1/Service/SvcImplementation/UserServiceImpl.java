package com.Qomoi1.Service.SvcImplementation;



import com.Qomoi1.Entity.UserEntity;
import com.Qomoi1.Repository.UserRepository;
import com.Qomoi1.Request.AddressDto;
import com.Qomoi1.Service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetailsService userDetailsService(){

        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username)  {
                return userRepository.findByEmail(username).orElseThrow(()-> new UsernameNotFoundException("User not found"));
            }
        };
    }

    @Override
    public String saveAddress(AddressDto addressDto, Long id) {

        Optional<UserEntity> userVal = userRepository.findById(id);
        if(userVal.isPresent()){
            userVal.get().setAddress1(addressDto.getAddress1());
            userVal.get().setAddress2(addressDto.getAddress2());
            userVal.get().setCity(addressDto.getCity());
            userVal.get().setState(addressDto.getState());
            userVal.get().setZipcode(addressDto.getZipcode());
            userRepository.save(userVal.get());
            return "Address saved successfully";
        }
        else{
            return "User not found";
        }
    }


}
