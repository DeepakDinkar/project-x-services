package com.qomoi.service;

import com.qomoi.dto.ExploreSearchDto;
import com.qomoi.entity.CoursesEntity;
import com.qomoi.entity.GlobalSearchEntity;
import com.qomoi.entity.VerticalEntity;

import java.util.List;

public interface SearchService {

    GlobalSearchEntity getGlobalSearchResults(String query);

    List<CoursesEntity> searchVerticals(String slug);

    ExploreSearchDto exploreSearch(String verticals,String courseName);
}
