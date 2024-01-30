package com.Qomoi1.Repository;


import com.Qomoi1.entity.CoursesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CourseRepository extends JpaRepository<CoursesEntity, Long> {


//    @Query(value = "SELECT c.id, c.campaign_template_course_name, c.campaign_template_rating, c.course_content, c.image_url, c.key_take_away, c.slug FROM courses c WHERE c.slug = :slug", nativeQuery = true )
//    public List<CourseResponse> getCourseByVerticals(@Param("slug") String slug);


    @Query(value = "SELECT JSON_BUILD_OBJECT("
            +"'verticals', JSON_BUILD_OBJECT("
            +" 'verticalId', t.id, 'verticalName', t.title, 'imageUrl',t.image_url, 'slug',t.slug, 'noOfCourses',t.no_of_courses), "
            +" 'course', JSON_BUILD_OBJECT("
            +" 'courseId', c.id, 'courseName', c.campaign_template_course_name, 'courseRating', c.campaign_template_rating, 'courseImages', c.image_url, 'keyTakeAway', c.key_take_away, 'slug',c.slug, 'courseContent',c.course_content ))"
            +" FROM verticals t "
            +" JOIN courses c ON t.id = c.id "
            +" ORDER BY t.id, c.campaign_template_rating DESC",nativeQuery = true)
    List<Map<String, Object>> findTopCoursesByVerticals();



}
