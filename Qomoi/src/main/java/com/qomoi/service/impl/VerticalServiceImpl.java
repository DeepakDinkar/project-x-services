package com.qomoi.service.impl;


import com.qomoi.entity.CoursesEntity;
import com.qomoi.entity.VerticalCoursesEntity;
import com.qomoi.repository.CourseRepository;
import com.qomoi.repository.VerticalRepository;
import com.qomoi.service.VerticalService;
import com.qomoi.entity.VerticalEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class VerticalServiceImpl implements VerticalService {

    private final VerticalRepository verticalRepository;
    private final CourseRepository courseRepository;

    public VerticalServiceImpl(VerticalRepository verticalRepository, CourseRepository courseRepository) {

        this.verticalRepository = verticalRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public List<VerticalEntity> getVerticals() {
        return verticalRepository.getAllVerticals();
    }

    @Override
    public void saveTopic(VerticalEntity verticalEntity) {
        verticalRepository.save(verticalEntity);
    }


    @Override
    public VerticalCoursesEntity getVerticalCoursesBySlug(String slug) {

        VerticalCoursesEntity verticalCoursesEntity = new VerticalCoursesEntity();
        VerticalEntity verticalEntity = verticalRepository.getVerticalEntityBySlug(slug);

        if(Objects.nonNull(verticalEntity)) {
            List<CoursesEntity> courses = courseRepository.findCoursesEntitiesBySlug(slug);
            verticalCoursesEntity.setSlug(verticalEntity.getSlug());
            verticalCoursesEntity.setTitle(verticalEntity.getTitle());
            verticalCoursesEntity.setImageUrl(verticalEntity.getImageUrl());
            verticalCoursesEntity.setCourses(courses);
        }

        return verticalCoursesEntity;
    }
}
