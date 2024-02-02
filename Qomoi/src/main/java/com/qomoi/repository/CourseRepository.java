package com.qomoi.repository;


import com.qomoi.entity.CoursesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<CoursesEntity, Long> {


    List<CoursesEntity> findTop2BySlugOrderByCampaignTemplateRating(String slug);

    List<CoursesEntity> findCoursesBySlug(String slug);

    Page<CoursesEntity> findCoursesEntitiesBySlug(String slug, PageRequest pageRequest);

    List<CoursesEntity> findTop3BySlugContainingIgnoreCaseOrCampaignTemplateCourseNameContainingIgnoreCase(String slug, String name);

    Page<CoursesEntity> findByCampaignTemplateCourseNameContainingIgnoreCase(String campaignTemplateCourseName, PageRequest pageRequest);

    Page<CoursesEntity> findAllByOrderByCampaignTemplateRatingDesc(PageRequest pageRequest);

    Page<CoursesEntity> findByCampaignTemplateCourseNameContainingIgnoreCaseAndSlugEquals(String campaignTemplateCourseName, String slug, PageRequest pageRequest);


    List<CoursesEntity> findBySlugContainingIgnoreCaseAndCampaignTemplateCourseNameContainingIgnoreCase(String slug, String name);

    List<CoursesEntity> findBySlugContainingIgnoreCase(String slug);

    List<CoursesEntity> findListByCampaignTemplateCourseNameContainingIgnoreCase(String name);

    List<CoursesEntity> findAllByOrderByCampaignTemplateRatingDesc();

}