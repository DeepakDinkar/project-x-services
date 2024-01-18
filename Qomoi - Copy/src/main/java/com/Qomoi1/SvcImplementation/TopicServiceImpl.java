package com.Qomoi1.SvcImplementation;

import com.Qomoi1.Entity.TopicEntity;
import com.Qomoi1.Repository.TopicRepository;
import com.Qomoi1.Repository.UserRepository;
import com.Qomoi1.Service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicServiceImpl implements TopicService {

    @Autowired
    TopicRepository topicRepository;

    @Override
    public List<TopicEntity> getAllTopics(){
     List<TopicEntity> allTopics =  topicRepository.findAll();
        return allTopics;
    }

    @Override
    public void saveTopic(TopicEntity topicEntity) {
        topicRepository.save(topicEntity);
    }


}
