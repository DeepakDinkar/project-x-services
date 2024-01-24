package com.Qomoi1.Entity;

import com.Qomoi1.Enum.DeliveryModes;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Entity
@Data
@Table(name = "courses")
@NoArgsConstructor
@AllArgsConstructor
public class CoursesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "courseId" )
    @SequenceGenerator(name = "courseId",sequenceName = "courseId",allocationSize = 1)
    @Column(name = "courseId")
    private Long courseId;

    @Column(name = "verticalId")
    private Long verticalId;

    @Column(name = "course_name")
    private String courseName;

    @Column(name = "course_desc",length = 500)
    private String courseDesc;

    @Column(name = "course_vertical",length = 500)
    private String courseVertical;

    @Column(name = "course_rating")
    private String courseRating;

    @Column(name = "course_images")
    private String courseImages;

    @Column(name = "key_take_away",length = 500)
    private String keyTakeAway;

    @Column(name = "accredited_by")
    private String accreditedBy;

    @Column(name = "delivery_methods")
    private DeliveryModes deliveryMethods;

    @Column(name = "course_dates")
    private Date courseDates;

    @Column(name = "course_city")
    private String courseCity;

    @Column(name = "course_outline")
    private String courseOutline;

    @Column(name = "who_should_attend",length = 500)
    private String whoShouldAttend;

}
