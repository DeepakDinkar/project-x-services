package com.qomoi.service.impl;

import com.qomoi.entity.CoursesEntity;
import com.qomoi.entity.GlobalSearchEntity;
import com.qomoi.entity.VerticalEntity;
import com.qomoi.repository.CourseRepository;
import com.qomoi.repository.VerticalRepository;
import com.qomoi.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    private final VerticalRepository verticalRepository;
    private final CourseRepository courseRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public SearchServiceImpl(VerticalRepository verticalRepository, CourseRepository courseRepository) {
        this.verticalRepository = verticalRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public GlobalSearchEntity getGlobalSearchResults(String query) {
        GlobalSearchEntity globalSearchEntity = new GlobalSearchEntity();

        if (!query.trim().equals("")) {
            List<VerticalEntity> verticals = verticalRepository.findTop3BySlugContainingIgnoreCase(query);
            List<CoursesEntity> courses = courseRepository.findTop3BySlugContainingIgnoreCaseOrCampaignTemplateCourseNameContainingIgnoreCase(query, query);

            globalSearchEntity.setVerticals(verticals);
            globalSearchEntity.setCourses(courses);
        }

        return globalSearchEntity;
    }

    @Override
    public List<CoursesEntity> searchVerticals(String slug) {

        List<CoursesEntity> list = this.jdbcTemplate.query("Select c.id, c.campaign_template_course_name, c.campaign_template_rating ,c.course_content,c.image_url, c.key_take_away, c.slug " + " FROM courses c WHERE LOWER(slug) = ?", new Object[]{slug},
                new RowMapper<CoursesEntity>() {
                    @Override
                    public CoursesEntity mapRow(ResultSet rs, int rowNum) throws SQLException {

                        CoursesEntity coursesEntity = new CoursesEntity();
                        coursesEntity.setId(rs.getLong("id"));
                        coursesEntity.setCampaignTemplateCourseName(rs.getString("campaign_template_course_name"));
                        coursesEntity.setCampaignTemplateRating(rs.getString("campaign_template_rating"));
                        coursesEntity.setCourseContent(rs.getString("course_content"));
                        coursesEntity.setImageUrl(rs.getString("image_url"));
                        coursesEntity.setSlug(rs.getString("slug"));
                        coursesEntity.setKeyTakeAway(Collections.singletonList(rs.getString("key_take_away")));

                        return coursesEntity;
                    }
                });

        return list;
    }

    @Override
    public Page<CoursesEntity> getExploreCourses(String slug, String query,PageRequest pageRequest) {

        if(StringUtils.hasText(slug) && StringUtils.hasText(query)) {
            return courseRepository.findByCampaignTemplateCourseNameContainingIgnoreCaseAndSlugEquals(query, slug,pageRequest);
        } else if(StringUtils.hasText(slug)) {
            return courseRepository.findCoursesEntitiesBySlug(slug,pageRequest);
        } else if(StringUtils.hasText(query)) {
            return courseRepository.findByCampaignTemplateCourseNameContainingIgnoreCase(query,pageRequest);
        }
        return courseRepository.findAllByOrderByCampaignTemplateRatingDesc(pageRequest);
    }

    @Override
    public List<CoursesEntity> getVerticalCourses(String slug, String query) {
        if(StringUtils.hasText(slug) && StringUtils.hasText(query)){
            return courseRepository.findBySlugContainingIgnoreCaseAndCampaignTemplateCourseNameContainingIgnoreCase(slug, query);
        }
        else if(StringUtils.hasText(slug)){
            return courseRepository.findBySlugContainingIgnoreCase(slug);
        }
        else if(StringUtils.hasText(query)){
            return courseRepository.findListByCampaignTemplateCourseNameContainingIgnoreCase(query);
        }
        return courseRepository.findAllByOrderByCampaignTemplateRatingDesc();
    }
}