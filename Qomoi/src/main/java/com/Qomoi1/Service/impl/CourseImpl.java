package com.Qomoi1.Service.impl;

import com.Qomoi1.Repository.CourseRepository;
import com.Qomoi1.Repository.VerticalRepository;
import com.Qomoi1.Service.CourseService;
import com.Qomoi1.dto.CourseResponse;
import com.Qomoi1.entity.CoursesEntity;
import com.Qomoi1.entity.TrendingVerticalEntity;
import com.Qomoi1.entity.VerticalEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
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
