package com.qomoi.controller;

import com.qomoi.entity.GlobalSearchEntity;
import com.qomoi.service.SearchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public ResponseEntity<GlobalSearchEntity> getAllQueryResults(@RequestParam(value = "query") String query) {
       GlobalSearchEntity globalSearchEntity = searchService.getGlobalSearchResults(query);

        return new ResponseEntity<>(globalSearchEntity, HttpStatus.OK);
    }

}
