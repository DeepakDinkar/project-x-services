package com.Qomoi1.Service.impl;

import com.Qomoi1.Repository.CourseRepository;
import com.Qomoi1.Service.CourseService;
import com.Qomoi1.dto.CourseResponse;
import com.Qomoi1.entity.CoursesEntity;
import com.Qomoi1.entity.VerticalEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
@Transactional
public class CourseImpl implements CourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;


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

    public List<CourseResponse> getCourseByVerticals(String slug) {

        StringBuilder sql = new StringBuilder("SELECT c.id, c.campaign_template_course_name, c.campaign_template_rating, c.course_content, c.image_url, c.key_take_away, c.slug FROM courses c WHERE c.slug = ?");

        List<CourseResponse> list = this.jdbcTemplate.query(sql.toString(), new Object[]{slug}, new RowMapper<CourseResponse>() {
            @Override
            public CourseResponse mapRow(ResultSet rs, int rowNum) throws SQLException {

                CourseResponse response = new CourseResponse();
                response.setId(rs.getLong("id"));
                response.setCampaignTemplateCourseName(rs.getString("campaign_template_course_name"));
                response.setCampaignTemplateRating(rs.getString("campaign_template_rating"));
                response.setCourseContent(rs.getString("course_content"));
                response.setImageUrl(rs.getString("image_url"));
                response.setKeyTakeAway(Collections.singletonList(rs.getString("key_take_away")));
                response.setSlug(rs.getString("slug"));

                return response;
            }
        });
        return list;
    }


    public List<Map<String, Object>> findTopCoursesByVerticals() {
       return courseRepository.findTopCoursesByVerticals();
    }


}
