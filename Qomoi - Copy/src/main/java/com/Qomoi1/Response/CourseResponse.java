package com.Qomoi1.Response;

import com.Qomoi1.Entity.CoursesEntity;
import com.Qomoi1.Enum.DeliveryModes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CourseResponse {

    private Long id;

    private String courseName;

    private String courseDesc;

    private String courseVertical;

    private String courseRating;

    private String courseImages;

    private String keyTakeAway;

    private String accreditedBy;

    private DeliveryModes deliveryMethods;

    private Date courseDates;

    private String courseCity;

    private String courseOutline;

    private String whoShouldAttend;

    public CourseResponse(CoursesEntity coursesEntity) {

        this.courseName = coursesEntity.getCourseName();
        this.accreditedBy = coursesEntity.getAccreditedBy();
        this.courseCity = coursesEntity.getCourseCity();
        this.courseDates = coursesEntity.getCourseDates();
        this.courseDesc = coursesEntity.getCourseDesc();
        this.courseOutline = coursesEntity.getCourseOutline();
        this.courseRating = coursesEntity.getCourseRating();
        this.whoShouldAttend = coursesEntity.getWhoShouldAttend();
        this.deliveryMethods = coursesEntity.getDeliveryMethods();
        this.courseImages = coursesEntity.getCourseImages();
        this.courseVertical = coursesEntity.getCourseVertical();

    }


}
