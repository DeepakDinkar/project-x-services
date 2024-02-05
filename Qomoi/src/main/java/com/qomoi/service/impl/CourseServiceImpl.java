package com.qomoi.service.impl;

import com.qomoi.dto.CourseLocationResponse;
import com.qomoi.repository.CourseRepository;
import com.qomoi.repository.VerticalRepository;
import com.qomoi.service.CourseService;
import com.qomoi.dto.CourseResponse;
import com.qomoi.entity.CoursesEntity;
import com.qomoi.entity.VerticalCoursesEntity;
import com.qomoi.entity.VerticalEntity;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

//import java.sql.ResultSet;
//import java.sql.SQLException;
import java.util.ArrayList;
//import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final VerticalRepository verticalRepository;
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;

    public CourseServiceImpl(CourseRepository courseRepository, VerticalRepository verticalRepository) {
        this.courseRepository = courseRepository;
        this.verticalRepository = verticalRepository;
    }


    @Override
    public Page<CoursesEntity> getAllCourse(PageRequest pageRequest) {
        return courseRepository.findAll(pageRequest);
    }

    //    @Override
//    public Page<CourseLocationResponse> getAllCourse(PageRequest pageRequest) {
//        StringBuilder sql = new StringBuilder("SELECT c.campaign_template_course_name, c.campaign_template_rating, c.course_add_date, ");
//        sql.append("c.course_content, c.image_url, c.is_trending, c.key_take_away, c.slug, l.location_name, l.date FROM courses ");
//        sql.append("c RIGHT JOIN location l ON c.id = l.course_id");
//
//        List<CourseLocationResponse> courseLocationResponses = this.jdbcTemplate.query(
//                sql.toString(),
//                new Object[]{},
//                new RowMapper<CourseLocationResponse>() {
//                    @Override
//                    public CourseLocationResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
//                        CourseLocationResponse courseLocationResponse = new CourseLocationResponse();
//                        courseLocationResponse.setCampaignTemplateCourseName(rs.getString("campaign_template_course_name"));
//                        courseLocationResponse.setCampaignTemplateRating(rs.getString("campaign_template_rating"));
//                        courseLocationResponse.setCourseAddDate(rs.getDate("course_add_date"));
//                        courseLocationResponse.setCourseContent(rs.getString("course_content"));
//                        courseLocationResponse.setImageUrl(rs.getString("image_url"));
//                        courseLocationResponse.setIsTrending(rs.getBoolean("is_trending"));
//                        courseLocationResponse.setKeyTakeAway(Collections.singletonList(rs.getString("key_take_away")));
//                        courseLocationResponse.setSlug(rs.getString("slug"));
//                        courseLocationResponse.setLocationName(rs.getString("location_name"));
//                        courseLocationResponse.setDate(rs.getDate("date"));
//                        return courseLocationResponse;
//                    }
//                }
//        );
//
//        // Perform pagination manually
//        int start = (int) pageRequest.getOffset();
//        int end = Math.min((start + pageRequest.getPageSize()), courseLocationResponses.size());
//        Page<CourseLocationResponse> page = new PageImpl<>(
//                courseLocationResponses.subList(start, end),
//                pageRequest,
//                courseLocationResponses.size()
//        );
//
//        return page;
//    }

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
        List<VerticalEntity> verticals = verticalRepository.findTop3ByOrderBySlugAsc();

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
