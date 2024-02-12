package com.qomoi.service.impl;

import com.qomoi.entity.CoursesEntity;
import com.qomoi.entity.GlobalSearchEntity;
import com.qomoi.entity.VerticalEntity;
import com.qomoi.repository.CourseRepository;
import com.qomoi.repository.LocationRepository;
import com.qomoi.repository.VerticalRepository;
import com.qomoi.service.SearchService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    private final VerticalRepository verticalRepository;
    private final CourseRepository courseRepository;
    private final LocationRepository locationRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    public SearchServiceImpl(VerticalRepository verticalRepository, CourseRepository courseRepository, LocationRepository locationRepository) {
        this.verticalRepository = verticalRepository;
        this.courseRepository = courseRepository;
        this.locationRepository = locationRepository;
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

        List<CoursesEntity> list = this.jdbcTemplate.query("Select c.id, c.campaign_template_course_name, c.campaign_template_rating ,c.course_content,c.image_url, c.key_take_away, c.slug " + " FROM courses c WHERE LOWER(slug) = ?", new Object[]{slug}, new RowMapper<CoursesEntity>() {
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
    public Page<CoursesEntity> getExploreCourses(String slug, String query, PageRequest pageRequest, Date fromDate, Date toDate, String location) {

        List<CoursesEntity> coursesEntityList = null;
        int start = 0;
        int end = 0;
        if (StringUtils.hasText(slug) && StringUtils.hasText(query)) {
            coursesEntityList = courseRepository.findByCampaignTemplateCourseNameContainingIgnoreCaseAndSlugEqualsOrderByIsTrendingDesc(query, slug);
            start = (int) pageRequest.getOffset();
            end = Math.min((start + pageRequest.getPageSize()), coursesEntityList.size());
            Page<CoursesEntity> coursesPage = new PageImpl<>(coursesEntityList.subList(start, end), pageRequest, coursesEntityList.size());
            return coursesPage;

        } else if (StringUtils.hasText(slug)) {
            coursesEntityList = courseRepository.findCoursesEntitiesBySlugOrderByIsTrendingDesc(slug);
            start = (int) pageRequest.getOffset();
            end = Math.min((start + pageRequest.getPageSize()), coursesEntityList.size());
            Page<CoursesEntity> coursesPage = new PageImpl<>(coursesEntityList.subList(start, end), pageRequest, coursesEntityList.size());
            return coursesPage;

        } else if (StringUtils.hasText(query)) {
            coursesEntityList = courseRepository.findByCampaignTemplateCourseNameContainingIgnoreCaseOrderByIsTrendingDesc(query);
            start = (int) pageRequest.getOffset();
            end = Math.min((start + pageRequest.getPageSize()), coursesEntityList.size());
            return new PageImpl<>(coursesEntityList.subList(start, end), pageRequest, coursesEntityList.size());
        } else if (fromDate != null && toDate != null) {
            return courseRepository.findByCourseAddedDateBetweenOrderByIsTrendingDesc(fromDate, toDate, pageRequest);
        } else if (StringUtils.hasText(location)) {
            List<CoursesEntity> list = this.jdbcTemplate.query(" Select DISTINCT(c.id), c.slug, c.campaign_template_course_name, c.course_content, c.campaign_template_rating, c.image_url, c.key_take_away, c.is_trending, c.course_added_date, l.location_name from location l FULL OUTER JOIN courses c ON c.id = l.course_id " + " where LOWER(location_name) = LOWER( ? ) order by is_trending desc ", new Object[]{location}, new RowMapper<CoursesEntity>() {
                @Override
                public CoursesEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
                    CoursesEntity coursesEntity = new CoursesEntity();
                    coursesEntity.setId(rs.getLong("id"));
                    coursesEntity.setSlug(rs.getString("slug"));
                    coursesEntity.setCampaignTemplateCourseName(rs.getString("campaign_template_course_name"));
                    coursesEntity.setCourseContent(rs.getString("course_content"));
                    coursesEntity.setCampaignTemplateRating(rs.getString("campaign_template_rating"));
                    coursesEntity.setImageUrl(rs.getString("image_url"));
                    coursesEntity.setKeyTakeAway(Collections.singletonList(rs.getString("key_take_away")));
                    coursesEntity.setIsTrending(rs.getBoolean("is_trending"));
                    coursesEntity.setCourseAddedDate(rs.getDate("course_added_date"));
                    return coursesEntity;
                }
            });
            start = (int) pageRequest.getOffset();
            end = Math.min((start + pageRequest.getPageSize()), list.size());
            Page<CoursesEntity> coursesPage = new PageImpl<>(list.subList(start, end), pageRequest, list.size());

            return coursesPage;

        }
        return courseRepository.findAllByOrderByCampaignTemplateRatingDesc(pageRequest);
    }

    @Override
    public Page<CoursesEntity> getVerticalCourses(String slug, String query, PageRequest pageRequest, Date fromDate, Date toDate, Boolean sortBy, String location) {
         if(StringUtils.hasText(slug) ) {

             StringBuilder sql = new StringBuilder("SELECT DISTINCT c.id, c.slug, c.campaign_template_course_name, c.course_content, c.campaign_template_rating, c.image_url, c.key_take_away, c.is_trending, c.course_added_date, ");
             sql.append("l.location_name, l.date ");
             sql.append(" FROM courses c JOIN location l ON c.id = l.course_id ");
             sql.append(" WHERE c.slug = :slug ");
             if(StringUtils.hasText(query)){
                 sql.append(" AND  LOWER(c.campaign_template_course_name) LIKE LOWER(:query)");
             }
             if( fromDate != null && toDate != null ){
                 sql.append(" AND c.course_added_date BETWEEN :fromDate AND :toDate ");
             }
             if( StringUtils.hasText(location)) {
                 sql.append( "AND LOWER(l.location_name) =  :location ");
             }
             if(sortBy != null && sortBy.equals(Boolean.TRUE)){
                 sql.append(" order by c.campaign_template_course_name ASC ");
             }
             if(sortBy != null && sortBy.equals(Boolean.FALSE)){
                 sql.append(" order by c.campaign_template_course_name DESC ");
             }
             Query queryRes = entityManager.createNativeQuery(sql.toString(), CoursesEntity.class);
             queryRes.setParameter("slug", slug);
             if(StringUtils.hasText(query)){
                 queryRes.setParameter("query", "%" + query + "%");
             }
             if(fromDate!=null){
                 queryRes.setParameter("fromDate", fromDate);
             }
             if(toDate!=null){
                 queryRes.setParameter("toDate", toDate);
             }
             if(StringUtils.hasText(location)){
                 queryRes.setParameter("location", location);
             }

             List<CoursesEntity> resultList = queryRes.getResultList();
             return paginateResultList(resultList, pageRequest);
         }
        else if (StringUtils.hasText(slug) && StringUtils.hasText(query)) {
            return courseRepository.findBySlugContainingIgnoreCaseAndCampaignTemplateCourseNameContainingIgnoreCaseOrderByIsTrendingDesc(slug, query, pageRequest);
        } else if (fromDate != null && toDate != null && StringUtils.hasText(slug)) {
            return courseRepository.findByCourseAddedDateBetweenAndSlugOrderByIsTrendingDesc(fromDate, toDate, slug, pageRequest);
        } else if (sortBy != null && StringUtils.hasText(slug)) {
            Sort sort = sortBy ? Sort.by(Sort.Direction.ASC, "campaignTemplateCourseName") : Sort.by(Sort.Direction.DESC, "campaignTemplateCourseName");
            return courseRepository.findBySlugOrderByCampaignTemplateCourseName(slug, pageRequest.withSort(sort));
        } else if (StringUtils.hasText(slug)) {
            return courseRepository.findBySlugContainingIgnoreCaseOrderByIsTrendingDesc(slug, pageRequest);
        } else if (StringUtils.hasText(query)) {
            return courseRepository.findListByCampaignTemplateCourseNameContainingIgnoreCase(query, pageRequest);
        }
        return courseRepository.findAllByOrderByIsTrendingDesc(pageRequest);
    }

    private Page<CoursesEntity> paginateResultList(List<CoursesEntity> resultList, Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<CoursesEntity> pageList;

        if (resultList.size() < startItem) {
            pageList = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, resultList.size());
            pageList = resultList.subList(startItem, toIndex);
        }

        return new PageImpl<>(pageList, PageRequest.of(currentPage, pageSize), resultList.size());
    }

}