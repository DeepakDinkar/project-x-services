package com.qomoi.service.impl;

import com.qomoi.dto.ExploreSearchDto;
import com.qomoi.entity.CoursesEntity;
import com.qomoi.entity.GlobalSearchEntity;
import com.qomoi.entity.VerticalEntity;
import com.qomoi.repository.CourseRepository;
import com.qomoi.repository.VerticalRepository;
import com.qomoi.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
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

    @Override
    public List<CoursesEntity> searchVerticals(String slug) {

        StringBuilder sql = new StringBuilder("Select c.id, c.campaign_template_course_name, c.campaign_template_rating ,c.course_content,c.image_url, c.key_take_away, c.slug ");
        sql.append(" FROM courses c WHERE LOWER(slug) = ?");

        List<CoursesEntity> list = this.jdbcTemplate.query(sql.toString(), new Object[]{slug},
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
    public ExploreSearchDto exploreSearch(String vertical, String courseName) {

        ExploreSearchDto exploreSearchDto = new ExploreSearchDto();
        List<VerticalEntity> verticals = null;
        List<CoursesEntity> courses = null;

        if (vertical != null && !vertical.trim().equals("")) {
            verticals = verticalRepository.findBySlugStartingWithIgnoreCase(vertical);
            exploreSearchDto.setVerticals(verticals);
        }else{
            exploreSearchDto.setVerticals(null);
        }

        if (courseName != null && !courseName.trim().equals("")) {
            courses = courseRepository.findCoursesByCampaignTemplateCourseNameStartingWithIgnoreCase(courseName);
            exploreSearchDto.setCourses(courses);
            } else {
            exploreSearchDto.setCourses(null);
            }

        return exploreSearchDto;
    }





}
