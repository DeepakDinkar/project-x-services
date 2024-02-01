package com.qomoi.service;

import com.qomoi.entity.GlobalSearchEntity;

public interface SearchService {

    GlobalSearchEntity getGlobalSearchResults(String query);
}
