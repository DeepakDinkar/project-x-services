package com.qomoi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseLocationResponse {

    private String slug;

    private String campaignTemplateCourseName;

    private String courseContent;

    private String campaignTemplateRating;

    private String imageUrl;

    private List<String> keyTakeAway;

    private Boolean isTrending;

    private Date courseAddDate;

    private String locationName;

    private Date date;

}
