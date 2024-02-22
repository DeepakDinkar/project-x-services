package com.qomoi.service.impl;

import com.qomoi.dto.CourseLocationResponse;
import com.qomoi.dto.LocationResponse;
import com.qomoi.dto.TrainerResponse;
import com.qomoi.entity.*;
import com.qomoi.repository.CourseRepository;
import com.qomoi.repository.LocationRepository;
import com.qomoi.repository.TrainersRepository;
import com.qomoi.repository.VerticalRepository;
import com.qomoi.service.CourseService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


@Service
@Transactional
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final VerticalRepository verticalRepository;
    private final LocationRepository locationRepository;

    private final TrainersRepository trainersRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public CourseServiceImpl(CourseRepository courseRepository, VerticalRepository verticalRepository,LocationRepository locationRepository, TrainersRepository trainersRepository) {
        this.courseRepository = courseRepository;
        this.verticalRepository = verticalRepository;
        this.locationRepository = locationRepository;
        this.trainersRepository = trainersRepository;
    }


    @Override
    public List<CourseLocationResponse> getAllCourse(PageRequest pageRequest) {
        StringBuilder sql = new StringBuilder("SELECT c.id, c.slug, c.campaign_template_course_name, c.course_content, c.campaign_template_rating, c.image_url, c.key_take_away, c.is_trending, c.course_added_date, l.location_name, l.date FROM courses c JOIN location l ON c.id = l.course_id");

        List<CourseLocationResponse> courseLocationResponses = new ArrayList<>();
        Map<Long, CourseLocationResponse> courseMap = new HashMap<>();

        this.jdbcTemplate.query(sql.toString(), new Object[]{}, new RowMapper<Void>() {
            @Override
            public Void mapRow(ResultSet rs, int rowNum) throws SQLException {
                long courseId = rs.getLong("id");

                if (!courseMap.containsKey(courseId)) {
                    CourseLocationResponse response = new CourseLocationResponse();
                    response.setId(courseId);
                    response.setSlug(rs.getString("slug"));
                    response.setCampaignTemplateCourseName(rs.getString("campaign_template_course_name"));
                    response.setCourseContent(rs.getString("course_content"));
                    response.setCampaignTemplateRating(rs.getString("campaign_template_rating"));
                    response.setImageUrl(rs.getString("image_url"));
                    response.setKeyTakeAway(Collections.singletonList(rs.getString("key_take_away")));
                    response.setIsTrending(rs.getBoolean("is_trending"));
                    response.setCourseAddedDate(rs.getDate("course_added_date"));
                    response.setLocation(new ArrayList<>());

                    courseMap.put(courseId, response);
                    courseLocationResponses.add(response);
                }

                LocationResponse locationResponse = new LocationResponse();
                locationResponse.setCourseId(courseId);
                locationResponse.setLocationName(rs.getString("location_name"));
                locationResponse.setDate(rs.getDate("date"));

                courseMap.get(courseId).getLocation().add(locationResponse);

                return null;
            }
        });

        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), courseLocationResponses.size());

        if (start >= courseLocationResponses.size()) {
            return Collections.emptyList();
        }
        return courseLocationResponses.subList(start, end);
    }



    @Override
    public Optional<CourseLocationResponse> getCourseId(Long id) {
        Optional<CoursesEntity> courseRes = courseRepository.findById(id);
        return courseRes.map(courseEntity -> {
            CourseLocationResponse courseLocationResponse = new CourseLocationResponse();
            courseLocationResponse.setId(courseEntity.getId());
            courseLocationResponse.setSlug(courseEntity.getSlug());
            courseLocationResponse.setCampaignTemplateCourseName(courseEntity.getCampaignTemplateCourseName());
            courseLocationResponse.setCourseContent(courseEntity.getCourseContent());
            courseLocationResponse.setCampaignTemplateRating(courseEntity.getCampaignTemplateRating());
            courseLocationResponse.setImageUrl(courseEntity.getImageUrl());
            courseLocationResponse.setKeyTakeAway(courseEntity.getKeyTakeAway());
            courseLocationResponse.setIsTrending(courseEntity.getIsTrending());
            courseLocationResponse.setCourseAddedDate(courseEntity.getCourseAddedDate());

            StringBuilder sql = new StringBuilder("SELECT location_name, date FROM location WHERE course_id = ?");
            List<LocationResponse> locationResponses = new ArrayList<>();
            this.jdbcTemplate.query(sql.toString(), new Object[]{id}, (rs, rowNum) -> {
                LocationResponse locationResponse = new LocationResponse();
                locationResponse.setCourseId(id);
                locationResponse.setLocationName(rs.getString("location_name"));
                locationResponse.setDate(rs.getDate("date"));
                locationResponses.add(locationResponse);
                return null;
            });

            courseLocationResponse.setLocation(locationResponses);

            StringBuilder sqlTrainer = new StringBuilder("SELECT trainer_name FROM trainers WHERE course_id = ?");
            List<TrainerResponse> trainerResponses = new ArrayList<>();
            this.jdbcTemplate.query(sqlTrainer.toString(), new Object[]{id}, (rs, rowNum) -> {
                TrainerResponse trainerResponse = new TrainerResponse();
                trainerResponse.setTrainerName(rs.getString("trainer_name"));
                trainerResponses.add(trainerResponse);
                return null;
            });

            courseLocationResponse.setTrainer(trainerResponses);

            return courseLocationResponse;
        });
    }


    public void saveCourse(CoursesEntity coursesEntity) {
        courseRepository.save(coursesEntity);
    }

    public List<CoursesEntity> getAllCoursesByVerticalSlug(String slug) {
        return new ArrayList<>();
    }


    @Override
    public List<CourseVerticalEntity> getTrendingVerticalCourses() {
        List<CourseVerticalEntity> trendingVerticalEntities = new ArrayList<>();
        List<VerticalEntity> verticals = verticalRepository.findTop3ByOrderBySlugAsc();

        verticals.forEach(verticalEntity -> {
            List<CoursesEntity> courses = courseRepository.findTop2BySlugOrderByCampaignTemplateRating(verticalEntity.getSlug());

            CourseVerticalEntity courseVerticalEntity = new CourseVerticalEntity();
            courseVerticalEntity.setSlug(verticalEntity.getSlug());
            courseVerticalEntity.setTitle(verticalEntity.getTitle());
            courseVerticalEntity.setImageUrl(verticalEntity.getImageUrl());

            courseVerticalEntity.setCourses(courses);

            trendingVerticalEntities.add(courseVerticalEntity);
        });
        return trendingVerticalEntities;

//        return courseRepository.findAllByOrderByIsTrendingDesc(pageRequest);

    }

    @Override
    public Page<CoursesEntity> getRecommendedCourses(PageRequest pageRequest) {
        return courseRepository.findAllByOrderByCampaignTemplateRatingDesc(pageRequest);
    }

    @Override
    public Page<CoursesEntity> getSimilarCourses(PageRequest pageRequest, String slug) {
        return courseRepository.findBySlugOrderByIsTrendingDesc(pageRequest, slug);
    }


    @Override
    public List<String> getAllLocation() {
        return locationRepository.findDistinctByLocationNameIsNotNull();
    }



    @Override
    public Page<TrainerResponse> getAllTrainers(PageRequest pageRequest) {

        return trainersRepository.findAll(pageRequest)
               .map(this::convertToTrainerResponse);
    }

    private TrainerResponse convertToTrainerResponse(TrainerEntity trainer) {
        TrainerResponse trainerResponse = new TrainerResponse();
        trainerResponse.setCourseId(trainer.getCourseId());
        trainerResponse.setTrainerName(trainer.getTrainerName());
        trainerResponse.setPhoneNumber(trainer.getPhoneNumber());
        trainerResponse.setImageUrl(trainer.getImageUrl());
        return trainerResponse;
    }
}
