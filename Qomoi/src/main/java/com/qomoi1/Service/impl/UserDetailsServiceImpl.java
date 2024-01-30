package com.qomoi1.Service.impl;


import com.qomoi1.Repository.UserRepository;
import com.qomoi1.entity.UserDE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDE user = userRepository.findByEmail(username);
        if(user == null){
            throw new UsernameNotFoundException("User Not Found with username: " + username);
        }
        return UserDetailsImpl.build(user);
    }


}