package com.qomoi.service.impl;

import com.qomoi.repository.CourseRepository;
import com.qomoi.repository.VerticalRepository;
import com.qomoi.service.CourseService;
import com.qomoi.dto.CourseResponse;
import com.qomoi.entity.CoursesEntity;
import com.qomoi.entity.TrendingVerticalEntity;
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
public class CourseImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final VerticalRepository verticalRepository;
    private final JdbcTemplate jdbcTemplate;

    public CourseImpl(CourseRepository courseRepository, VerticalRepository verticalRepository, JdbcTemplate jdbcTemplate) {
        this.courseRepository = courseRepository;
        this.verticalRepository = verticalRepository;
        this.jdbcTemplate = jdbcTemplate;
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
    public List<TrendingVerticalEntity> getTrendingVerticalCourses() {
        List<TrendingVerticalEntity> trendingVerticalEntities = new ArrayList<>();

        List<VerticalEntity> verticals = verticalRepository.getTop3VerticalEntities();

        verticals.forEach(verticalEntity -> {
            List<CoursesEntity> courses = courseRepository.findTop2BySlugOrderByCampaignTemplateRating(verticalEntity.getSlug());

            TrendingVerticalEntity trendingVerticalEntity = new TrendingVerticalEntity();
            trendingVerticalEntity.setSlug(verticalEntity.getSlug());
            trendingVerticalEntity.setTitle(verticalEntity.getTitle());
            trendingVerticalEntity.setImageUrl(verticalEntity.getImageUrl());
            trendingVerticalEntity.setCourses(courses);

            trendingVerticalEntities.add(trendingVerticalEntity);
        });
        return trendingVerticalEntities;
    }
}
