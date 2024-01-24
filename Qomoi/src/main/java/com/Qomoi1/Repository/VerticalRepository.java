package com.Qomoi1.Repository;

import com.Qomoi1.Entity.VerticalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VerticalRepository extends JpaRepository<VerticalEntity,Long> {

    @Query(value = "SELECT * FROM verticals WHERE topic_name LIKE %: query% ", nativeQuery = true)
    public List<VerticalEntity> searchQuery(@Param("query") String query);
}
