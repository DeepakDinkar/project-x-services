package com.Qomoi1.Repository;

import com.Qomoi1.Entity.CoursesEntity;
import com.Qomoi1.Response.CourseResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<CoursesEntity, Long> {


    @Query(value = "SELECT c FROM courses c WHERE c.slug = :slug", nativeQuery = true )
    public List<CourseResponse> getCourseByVerticals(@Param("slug") String slug);


    @Query(value = "SELECT JSON_BUILD_OBJECT('verticals', JSON_BUILD_OBJECT('id', v.id, 'title', v.title,'slug',v.slug,'imageUrl',v.image_url),"
            +"'course', JSON_BUILD_OBJECT('id', c.id,'campaignTemplateCourseName', c.campaign_template_courseName,"
            +"'courseContent', c.course_content,'campaignTemplateRating', c.campaign_template_rating,"
            +"'imageUrl', c.image_url,'keyTakeAway', c.key_take_away))"
            +"FROM verticals t"
            +"JOIN courses c ON t.slug = c.slug"
            +"ORDER BY t.id, c.campaign_template_rating DESC;",nativeQuery = true)
    List<Object[]> findTopCoursesByVerticals();



}
