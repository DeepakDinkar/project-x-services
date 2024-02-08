package com.qomoi.repository;


import com.qomoi.entity.CoursesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<CoursesEntity, Long> {

    List<CoursesEntity> findTop2BySlugOrderByCampaignTemplateRating(String slug);

    Optional<List<CoursesEntity>> findCoursesBySlug(String slug);

    List<CoursesEntity> findCoursesEntitiesBySlugOrderByIsTrendingDesc(String slug);

    List<CoursesEntity> findTop3BySlugContainingIgnoreCaseOrCampaignTemplateCourseNameContainingIgnoreCase(String slug, String name);

    List<CoursesEntity> findByCampaignTemplateCourseNameContainingIgnoreCaseOrderByIsTrendingDesc(String campaignTemplateCourseName);

    Page<CoursesEntity> findAllByOrderByCampaignTemplateRatingDesc(PageRequest pageRequest);

    List<CoursesEntity> findByCampaignTemplateCourseNameContainingIgnoreCaseAndSlugEqualsOrderByIsTrendingDesc(String campaignTemplateCourseName, String slug);


    Page<CoursesEntity> findBySlugContainingIgnoreCaseAndCampaignTemplateCourseNameContainingIgnoreCaseOrderByIsTrendingDesc(String slug, String name, PageRequest pageRequest);

    Page<CoursesEntity> findBySlugContainingIgnoreCaseOrderByIsTrendingDesc(String slug, PageRequest pageRequest);

    Page<CoursesEntity> findListByCampaignTemplateCourseNameContainingIgnoreCase(String name, PageRequest pageRequest);

    Page<CoursesEntity> findByCourseAddedDateBetweenOrderByIsTrendingDesc(Date startDate, Date endDate,PageRequest pageRequest);

    Page<CoursesEntity> findByCourseAddedDateBetweenAndSlugOrderByIsTrendingDesc(Date startDate, Date endDate, String slug, PageRequest pageRequest);



}