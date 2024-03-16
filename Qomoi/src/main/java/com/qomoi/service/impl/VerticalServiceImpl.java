package com.qomoi.service.impl;


import com.qomoi.dto.CourseLocationResponse;
import com.qomoi.dto.LocationResponse;
import com.qomoi.dto.TrainerResponse;
import com.qomoi.entity.CoursesEntity;
import com.qomoi.entity.VerticalCoursesEntity;
import com.qomoi.entity.VerticalEntity;
import com.qomoi.repository.CourseRepository;
import com.qomoi.repository.VerticalRepository;
import com.qomoi.service.VerticalService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class VerticalServiceImpl implements VerticalService {

    private final VerticalRepository verticalRepository;
    private final CourseRepository courseRepository;
    private final JdbcTemplate jdbcTemplate;

    public VerticalServiceImpl(VerticalRepository verticalRepository, CourseRepository courseRepository, JdbcTemplate jdbcTemplate) {

        this.verticalRepository = verticalRepository;
        this.courseRepository = courseRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<VerticalEntity> getVerticals() {
        return verticalRepository.getAllVerticals();
    }

    @Override
    public void saveTopic(VerticalEntity verticalEntity) {
        verticalRepository.save(verticalEntity);
    }


    @Override
    public VerticalCoursesEntity getVerticalCoursesBySlug(String slug) {

        VerticalCoursesEntity verticalCoursesEntity = new VerticalCoursesEntity();
        VerticalEntity verticalEntity = verticalRepository.getVerticalEntityBySlug(slug);

        if (Objects.nonNull(verticalEntity)) {
            Optional<List<CoursesEntity>> coursesListOptional = courseRepository.findCoursesBySlug(slug);

            coursesListOptional.ifPresent(coursesList -> {
                verticalCoursesEntity.setSlug(verticalEntity.getSlug());
                verticalCoursesEntity.setTitle(verticalEntity.getTitle());
                verticalCoursesEntity.setImageUrl(verticalEntity.getImageUrl());

                List<CourseLocationResponse> courseLocationResponses = new ArrayList<>();

                for (CoursesEntity coursesEntity : coursesList) {
                    CourseLocationResponse courseLocationResponse = new CourseLocationResponse();
                    courseLocationResponse.setId(coursesEntity.getId());
                    courseLocationResponse.setSlug(coursesEntity.getSlug());
                    courseLocationResponse.setCourseAmt(coursesEntity.getCourseAmt());
                    courseLocationResponse.setCampaignTemplateCourseName(coursesEntity.getCampaignTemplateCourseName());
                    courseLocationResponse.setCourseContent(coursesEntity.getCourseContent());
                    courseLocationResponse.setCampaignTemplateRating(coursesEntity.getCampaignTemplateRating());
                    courseLocationResponse.setImageUrl(coursesEntity.getImageUrl());
                    courseLocationResponse.setKeyTakeAway(coursesEntity.getKeyTakeAway());
                    courseLocationResponse.setIsTrending(coursesEntity.getIsTrending());
                    courseLocationResponse.setCourseAddedDate(coursesEntity.getCourseAddedDate());

                    List<LocationResponse> locationResponses = new ArrayList<>();
                    this.jdbcTemplate.query("SELECT location_name, date FROM location WHERE course_id = ?", new Object[]{coursesEntity.getId()}, (rs, rowNum) -> {
                        LocationResponse locationResponse = new LocationResponse();
                        locationResponse.setCourseId(coursesEntity.getId());
                        locationResponse.setLocationName(rs.getString("location_name"));
                        locationResponse.setDate(rs.getDate("date"));
                        locationResponses.add(locationResponse);
                        return null;
                    });
                    courseLocationResponse.setLocation(locationResponses);

                    List<TrainerResponse> trainerResponses = new ArrayList<>();
                    this.jdbcTemplate.query("SELECT trainer_name,email,image_url,phone_number FROM trainers WHERE ? = ANY(course_id)", new Object[]{coursesEntity.getId()}, (rs, rowNum) -> {
                        TrainerResponse trainerResponse = new TrainerResponse();
                        trainerResponse.setTrainerName(rs.getString("trainer_name"));
                        trainerResponse.setEmail(rs.getString("email"));
                        trainerResponse.setPhoneNumber(rs.getString("phone_number"));
                        trainerResponse.setImageUrl(rs.getString("image_url"));
                        trainerResponses.add(trainerResponse);
                        return null;
                    });
                    courseLocationResponse.setTrainer(trainerResponses);

                    courseLocationResponses.add(courseLocationResponse);
                }

                verticalCoursesEntity.setCourses(courseLocationResponses);
            });
        }

        return verticalCoursesEntity;
    }


}