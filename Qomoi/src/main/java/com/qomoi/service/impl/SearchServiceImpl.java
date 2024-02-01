package com.qomoi.service.impl;

import com.qomoi.entity.CoursesEntity;
import com.qomoi.entity.GlobalSearchEntity;
import com.qomoi.entity.VerticalEntity;
import com.qomoi.repository.CourseRepository;
import com.qomoi.repository.VerticalRepository;
import com.qomoi.service.SearchService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    private final VerticalRepository verticalRepository;

    private final CourseRepository courseRepository;

    public SearchServiceImpl(VerticalRepository verticalRepository, CourseRepository courseRepository) {
        this.verticalRepository = verticalRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public GlobalSearchEntity getGlobalSearchResults(String query) {
        GlobalSearchEntity globalSearchEntity = new GlobalSearchEntity();

        if(!query.trim().equals("")) {
            List<VerticalEntity> verticals = verticalRepository.findTop3BySlugContainingIgnoreCase(query);
            List<CoursesEntity> courses = courseRepository.findTop3BySlugContainingIgnoreCaseOrCampaignTemplateCourseNameContainingIgnoreCase(query, query);

            globalSearchEntity.setVerticals(verticals);
            globalSearchEntity.setCourses(courses);
        }

        return globalSearchEntity;
    }
}
