package com.qomoi.repository;

import com.qomoi.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocationRepository extends JpaRepository<LocationEntity, Long> {

    @Query(value = "SELECT course_id FROM location WHERE LOWER(location_name) = LOWER(:locationName)",nativeQuery = true)
    List<Long> findByLocationNameIgnoreCase(@Param("locationName") String locationName);

    @Query(value = "SELECT DISTINCT l.location_name FROM location l WHERE l.location_name IS NOT NULL", nativeQuery = true)
    List<String> findDistinctByLocationNameIsNotNull();


}
