package com.qomoi.service;

import com.qomoi.dto.ExploreSearchDto;
import com.qomoi.entity.CoursesEntity;
import com.qomoi.entity.GlobalSearchEntity;

import java.util.List;

public interface SearchService {

    GlobalSearchEntity getGlobalSearchResults(String query);

    List<CoursesEntity> searchVerticals(String slug);

    List<CoursesEntity> getExploreCourses(String slug, String query);
}
