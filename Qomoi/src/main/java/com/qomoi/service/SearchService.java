package com.qomoi.service;

import com.qomoi.dto.ExploreSearchDto;
import com.qomoi.entity.CoursesEntity;
import com.qomoi.entity.GlobalSearchEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Date;
import java.util.List;

public interface SearchService {

    GlobalSearchEntity getGlobalSearchResults(String query);

    List<CoursesEntity> searchVerticals(String slug);

    Page<CoursesEntity> getExploreCourses(String slug, String query, PageRequest pageRequest, Date fromDate, Date toDate, String location);

    Page<CoursesEntity> getVerticalCourses(String slug, String query, PageRequest pageRequest,Date fromDate, Date toDate, Boolean sortBy);
}