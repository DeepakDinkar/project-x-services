package com.Qomoi1.Service;

import com.Qomoi1.Request.AddressDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {
    UserDetailsService userDetailsService();

    String saveAddress(AddressDto addressDto, Long id);
}
