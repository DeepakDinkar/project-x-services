package com.Qomoi1.Service;

import com.Qomoi1.Entity.TopicEntity;

import java.util.List;

public interface TopicService {

    List<TopicEntity> getAllTopics();

    void saveTopic(TopicEntity topicEntity);
}
