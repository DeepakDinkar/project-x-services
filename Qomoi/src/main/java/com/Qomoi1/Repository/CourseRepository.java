package com.Qomoi1.Repository;


import com.Qomoi1.entity.CoursesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<CoursesEntity, Long> {


    List<CoursesEntity> findTop2BySlugOrderByCampaignTemplateRating(String slug);
}
