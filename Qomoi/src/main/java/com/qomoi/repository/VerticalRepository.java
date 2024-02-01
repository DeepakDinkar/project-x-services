package com.qomoi.repository;


import com.qomoi.entity.VerticalCoursesEntity;
import com.qomoi.entity.VerticalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VerticalRepository extends JpaRepository<VerticalEntity, Long> {

    @Query(value = """
            SELECT
            	   t.id,
                   t.slug,
                   t.title,
                   t.image_url,
                   COUNT(c.slug) AS no_of_courses
            FROM
                verticals t
            LEFT JOIN
                courses c ON t.slug = c.slug
            GROUP BY
                t.slug, t.id;
            """, nativeQuery = true)
    List<VerticalEntity> getAllVerticals();


    @Query(value = "SELECT * FROM courses WHERE LOWER(slug) LIKE %:query%", nativeQuery = true)
    List<VerticalEntity> searchQuery(@Param("query") String query);

    @Query(value = "SELECT * FROM verticals LIMIT 3", nativeQuery = true)
     List<VerticalEntity> getTop3VerticalEntities();

    VerticalEntity getVerticalEntityBySlug(String slug);

    List<VerticalEntity> findTop3BySlugContainingIgnoreCase(String slug);

    List<VerticalEntity> findBySlugStartingWithIgnoreCase(String slug);

    }