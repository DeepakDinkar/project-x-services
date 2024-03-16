package com.qomoi.service;

import com.qomoi.repository.LocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CronService {

    private final LocationRepository locationRepository;

    public CronService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Transactional
    public void deletePrevDates(){
        locationRepository.deletePrevDates();
    }

}
