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

    List<CoursesEntity> findCoursesBySlug(String slug);

    Page<CoursesEntity> findCoursesEntitiesBySlug(String slug, PageRequest pageRequest);

    List<CoursesEntity> findTop3BySlugContainingIgnoreCaseOrCampaignTemplateCourseNameContainingIgnoreCase(String slug, String name);

    Page<CoursesEntity> findByCampaignTemplateCourseNameContainingIgnoreCase(String campaignTemplateCourseName, PageRequest pageRequest);

    Page<CoursesEntity> findAllByOrderByCampaignTemplateRatingDesc(PageRequest pageRequest);

    Page<CoursesEntity> findByCampaignTemplateCourseNameContainingIgnoreCaseAndSlugEquals(String campaignTemplateCourseName, String slug, PageRequest pageRequest);


    Page<CoursesEntity> findBySlugContainingIgnoreCaseAndCampaignTemplateCourseNameContainingIgnoreCase(String slug, String name, PageRequest pageRequest);

    Page<CoursesEntity> findBySlugContainingIgnoreCase(String slug, PageRequest pageRequest);

    Page<CoursesEntity> findListByCampaignTemplateCourseNameContainingIgnoreCase(String name, PageRequest pageRequest);

    Page<CoursesEntity> findByCourseAddedDateBetweenOrderByIsTrendingDesc(Date startDate, Date endDate,PageRequest pageRequest);


    @Query(value = "SELECT * FROM courses WHERE id = :id ORDER BY is_trending DESC", nativeQuery = true)
    Optional<CoursesEntity> findByIdWithTrending(@Param("id") Long id);


}