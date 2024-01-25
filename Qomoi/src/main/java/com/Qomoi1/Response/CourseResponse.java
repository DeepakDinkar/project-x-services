package com.Qomoi1.Response;

import com.Qomoi1.Entity.CoursesEntity;
import com.Qomoi1.Enum.DeliveryModes;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CourseResponse {

    private Long id;

    private String slug;

    private String campaignTemplateCourseName;

    private String courseContent;

    private String campaignTemplateRating;

    private String imageUrl;

    private List<String> keyTakeAway;

    public CourseResponse(CoursesEntity coursesEntity) {


      this.id = coursesEntity.getId();
      this.campaignTemplateCourseName = coursesEntity.getCampaignTemplateCourseName();
      this.campaignTemplateRating = coursesEntity.getCampaignTemplateRating();
      this.courseContent = coursesEntity.getCourseContent();
      this.imageUrl = coursesEntity.getImageUrl();
      this.slug = coursesEntity.getSlug();
      this.keyTakeAway = coursesEntity.getKeyTakeAway();

    }


}
