package com.qomoi.repository;

import com.qomoi.entity.MyCoursesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyCourseRepository extends JpaRepository<MyCoursesEntity, Long> {

    boolean existsByEmail(String email);

    MyCoursesEntity findByEmail(String email);
}
