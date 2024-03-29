package com.qomoi.service.impl;

import com.qomoi.dto.CourseLocationResponse;
import com.qomoi.dto.LocationResponse;
import com.qomoi.dto.TrainerResponse;
import com.qomoi.entity.CourseVerticalEntity;
import com.qomoi.entity.CoursesEntity;
import com.qomoi.entity.VerticalEntity;
import com.qomoi.repository.CourseRepository;
import com.qomoi.repository.LocationRepository;
import com.qomoi.repository.VerticalRepository;
import com.qomoi.service.CourseService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

    private final JdbcTemplate jdbcTemplate;

    public CourseServiceImpl(CourseRepository courseRepository, VerticalRepository verticalRepository, LocationRepository locationRepository, JdbcTemplate jdbcTemplate) {
        this.courseRepository = courseRepository;
        this.verticalRepository = verticalRepository;
        this.locationRepository = locationRepository;
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<CourseLocationResponse> getAllCourse(PageRequest pageRequest) {

        List<CourseLocationResponse> courseLocationResponses = new ArrayList<>();
        Map<Long, CourseLocationResponse> courseMap = new HashMap<>();

        this.jdbcTemplate.query("SELECT c.id, c.slug, c.campaign_template_course_name, c.course_content, c.campaign_template_rating, c.image_url, c.key_take_away, c.is_trending, c.course_added_date, l.location_name, l.date FROM courses c JOIN location l ON c.id = l.course_id", new Object[]{}, new RowMapper<Void>() {
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
            courseLocationResponse.setCourseAmt(courseEntity.getCourseAmt());

            List<LocationResponse> locationResponses = new ArrayList<>();
            this.jdbcTemplate.query("SELECT location_name, date FROM location WHERE course_id = ?", new Object[]{id}, (rs, rowNum) -> {
                LocationResponse locationResponse = new LocationResponse();
                locationResponse.setCourseId(id);
                locationResponse.setLocationName(rs.getString("location_name"));
                locationResponse.setDate(rs.getDate("date"));
                locationResponses.add(locationResponse);
                return null;
            });

            courseLocationResponse.setLocation(locationResponses);

            List<TrainerResponse> trainerResponses = new ArrayList<>();
            this.jdbcTemplate.query("SELECT trainer_name,email,image_url,phone_number FROM trainers WHERE ? = ANY(course_id)", new Object[]{id}, (rs, rowNum) -> {
                TrainerResponse trainerResponse = new TrainerResponse();
                trainerResponse.setTrainerName(rs.getString("trainer_name"));
                trainerResponse.setEmail(rs.getString("email"));
                trainerResponse.setPhoneNumber(rs.getString("phone_number"));
                trainerResponse.setImageUrl(rs.getString("image_url"));
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


    }

    @Override
    public Page<CoursesEntity> getAllTrending(PageRequest pageRequest) {
        return courseRepository.findAllByOrderByIsTrendingDesc(pageRequest);

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
        String sql = "SELECT string_agg(c.campaign_template_course_name, ', ') AS course_names, t.trainer_name, t.phone_number, t.image_url, t.email  " + " FROM trainers t LEFT JOIN courses c ON c.id = ANY(t.course_id) " +
                     " GROUP BY t.trainer_name, t.phone_number, t.image_url,t.email";

        List<TrainerResponse> trainerResponses = this.jdbcTemplate.query(sql, new Object[]{}, new RowMapper<TrainerResponse>() {
            @Override
            public TrainerResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
                TrainerResponse trainerResponse = new TrainerResponse();
                trainerResponse.setCourseName(Collections.singletonList(rs.getString("course_names")));
                trainerResponse.setTrainerName(rs.getString("trainer_name"));
                trainerResponse.setImageUrl(rs.getString("image_url"));
                trainerResponse.setPhoneNumber(rs.getString("phone_number"));
                trainerResponse.setEmail(rs.getString("email"));
                return trainerResponse;
            }
        });

        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), trainerResponses.size());

        return new PageImpl<>(trainerResponses.subList(start, end), pageRequest, trainerResponses.size());
    }

}
