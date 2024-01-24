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


    @Query(value = "SELECT c FROM courses c WHERE c.topicId = :id", nativeQuery = true )
    public List<CourseResponse> getCourseByTopic(@Param("id") Long id);


    @Query(value = "SELECT JSON_BUILD_OBJECT('verticals', JSON_BUILD_OBJECT('verticalId', t.vertical_id,'verticalName', t.vertical_name),"
            +"'course', JSON_BUILD_OBJECT('courseId', c.course_id,'courseName', c.course_name,"
            +"'courseDesc', c.course_desc,'courseVertical', c.course_vertical,"
            +"'courseRating', c.course_rating,'courseImages', c.course_images,"
            +"'keyTakeAway', c.key_take_away,'accreditedBy', c.accredited_by, "
            +"'deliveryMethods', c.delivery_methods,'courseDates', c.course_dates,"
            +"'courseCity', c.course_city,'courseOutline', c.course_outline,"
            +"'whoShouldAttend', c.who_should_attend))"
            +"FROM verticals t"
            +"JOIN courses c ON t.vertical_id = c.vertical_id"
            +"ORDER BY t.vertical_id, c.course_rating DESC;",nativeQuery = true)
    List<Object[]> findTopCoursesByTopic();



}
