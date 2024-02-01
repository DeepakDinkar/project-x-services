package com.qomoi.service.impl;

import com.qomoi.repository.CourseRepository;
import com.qomoi.repository.VerticalRepository;
import com.qomoi.service.CourseService;
import com.qomoi.dto.CourseResponse;
import com.qomoi.entity.CoursesEntity;
import com.qomoi.entity.VerticalCoursesEntity;
import com.qomoi.entity.VerticalEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final VerticalRepository verticalRepository;

    public CourseServiceImpl(CourseRepository courseRepository, VerticalRepository verticalRepository) {
        this.courseRepository = courseRepository;
        this.verticalRepository = verticalRepository;
    }


    @Override
    public Page<CoursesEntity> getAllCourse(PageRequest pageRequest) {
        return courseRepository.findAll(pageRequest);
    }

    @Override
    public Optional<CourseResponse> getCourseId(Long id) {
        Optional<CoursesEntity> courseRes = courseRepository.findById(id);
        return courseRes.map(CourseResponse::new);

    }

    public void saveCourse(CoursesEntity coursesEntity) {
        courseRepository.save(coursesEntity);
    }

    public List<CoursesEntity> getAllCoursesByVerticalSlug(String slug) {
        return new ArrayList<>();
    }


    @Override
    public List<VerticalCoursesEntity> getTrendingVerticalCourses() {
        List<VerticalCoursesEntity> trendingVerticalEntities = new ArrayList<>();
        List<VerticalEntity> verticals = verticalRepository.getTop3VerticalEntities();

        verticals.forEach(verticalEntity -> {
            List<CoursesEntity> courses = courseRepository.findTop2BySlugOrderByCampaignTemplateRating(verticalEntity.getSlug());

            VerticalCoursesEntity verticalCoursesEntity = new VerticalCoursesEntity();
            verticalCoursesEntity.setSlug(verticalEntity.getSlug());
            verticalCoursesEntity.setTitle(verticalEntity.getTitle());
            verticalCoursesEntity.setImageUrl(verticalEntity.getImageUrl());
            verticalCoursesEntity.setCourses(courses);

            trendingVerticalEntities.add(verticalCoursesEntity);
        });
        return trendingVerticalEntities;
    }
}
