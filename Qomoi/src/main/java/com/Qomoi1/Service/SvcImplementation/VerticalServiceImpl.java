package com.Qomoi1.Service.SvcImplementation;

import com.Qomoi1.Entity.VerticalEntity;
import com.Qomoi1.Repository.VerticalRepository;
import com.Qomoi1.Service.VerticalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VerticalServiceImpl implements VerticalService {

    @Autowired
    VerticalRepository verticalRepository;

    @Override
    public List<VerticalEntity> getAllTopics(){
     List<VerticalEntity> allTopics =  verticalRepository.findAll();
        return allTopics;
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
