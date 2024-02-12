package com.qomoi.controller;

import com.qomoi.entity.CoursesEntity;
import com.qomoi.entity.GlobalSearchEntity;
import com.qomoi.service.SearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

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

    @GetMapping("/{slug}")
    public ResponseEntity<List<CoursesEntity>> searchVerticals(@PathVariable String slug) {
        if (slug != null) {
            List<CoursesEntity> response = searchService.searchVerticals(slug);
            return ResponseEntity.ok(response);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/explore/{page}")
    public ResponseEntity<Page<CoursesEntity>> getExploreCourses(
            @PathVariable int page,
            @RequestParam(name = "slug", required = false) String slug,
            @RequestParam(name = "query", required = false) String query,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "fromDate", required = false) Date fromDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "toDate", required = false) Date toDate,
            @RequestParam(name = "location", required = false) String location) {
        int pageSize = 12;
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize);

        Page<CoursesEntity> coursesPage = searchService.getExploreCourses(slug, query, pageRequest, fromDate, toDate, location);
        return new ResponseEntity<>(coursesPage, HttpStatus.OK);
    }


    @GetMapping("/verticals/{slug}/{page}")
    public ResponseEntity<Page<CoursesEntity>> getVerticalCourses(@PathVariable String slug, @PathVariable int page, @RequestParam(name = "query", required = false) String query,
                                                                  @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "fromDate", required = false) Date fromDate,
                                                                  @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "toDate", required = false) Date toDate,
                                                                  @RequestParam(name = "sortBy", required = false) Boolean sortBy,
                                                                  @RequestParam(name = "location" , required = false) String location) {
        int pageSize = 12;
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
        Page<CoursesEntity> verticalPage = searchService.getVerticalCourses(slug, query, pageRequest, fromDate, toDate, sortBy,location);
        return new ResponseEntity<>(verticalPage, HttpStatus.OK);
    }
}
