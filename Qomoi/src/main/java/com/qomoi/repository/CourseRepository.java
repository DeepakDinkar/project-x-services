package com.qomoi.repository;


import com.qomoi.entity.CoursesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<CoursesEntity, Long> {


    List<CoursesEntity> findTop2BySlugOrderByCampaignTemplateRating(String slug);

    List<CoursesEntity> findCoursesEntitiesBySlug(String slug);

    List<CoursesEntity> findTop3BySlugContainingIgnoreCaseOrCampaignTemplateCourseNameContainingIgnoreCase(String slug, String name);

    List<CoursesEntity> findByCampaignTemplateCourseNameContainingIgnoreCase(String campaignTemplateCourseName);

    List<CoursesEntity> findAllByOrderByCampaignTemplateRatingDesc();

    List<CoursesEntity> findByCampaignTemplateCourseNameContainingIgnoreCaseAndSlugEquals(String campaignTemplateCourseName, String slug);
}
