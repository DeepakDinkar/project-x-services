package com.qomoi.controller;

import com.qomoi.dto.ExploreSearchDto;
import com.qomoi.entity.CoursesEntity;
import com.qomoi.entity.GlobalSearchEntity;
import com.qomoi.entity.VerticalEntity;
import com.qomoi.service.SearchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/explore")
    public ResponseEntity<ExploreSearchDto> exploreSearch(
            @RequestParam(name = "verticals" , required = false) String verticals,
            @RequestParam(name = "courseName" , required = false) String  courseName
    ){
        ExploreSearchDto exploreSearchDto = searchService.exploreSearch(verticals,courseName);
        if(exploreSearchDto != null){
            return new ResponseEntity<>(exploreSearchDto, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }



}
