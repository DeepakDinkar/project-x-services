package com.qomoi.service;

import com.qomoi.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CronService {

    @Autowired
    private LocationRepository locationRepository;

    @Transactional
    public void deletePrevDates(){
        locationRepository.deletePrevDates();
    }

}
