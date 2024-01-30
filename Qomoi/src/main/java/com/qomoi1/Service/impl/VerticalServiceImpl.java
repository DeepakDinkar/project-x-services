package com.qomoi1.Service.impl;


import com.qomoi1.Repository.VerticalRepository;
import com.qomoi1.Service.VerticalService;
import com.qomoi1.entity.VerticalEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VerticalServiceImpl implements VerticalService {

    @Autowired
    VerticalRepository verticalRepository;

    @Override
    public List<VerticalEntity> getVerticals() {
        return verticalRepository.getAllVerticals();
    }

    @Override
    public void saveTopic(VerticalEntity verticalEntity) {
        verticalRepository.save(verticalEntity);
    }


    @Override
    public List<VerticalEntity> searchVerticals(String query) {
        List<VerticalEntity> verticalList = verticalRepository.searchQuery(query);
        return verticalList;
    }
}