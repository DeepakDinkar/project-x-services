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


    @Query(value = "SELECT c FROM CoursesEntity c WHERE c.topicId = :id", nativeQuery = true )
    public List<CourseResponse> getCourseByTopic(@Param("id") Long id);


}
