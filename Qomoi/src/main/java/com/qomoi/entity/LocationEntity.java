package com.qomoi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "location")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationEntity {

@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "locationId")
@SequenceGenerator(name = "locationId",sequenceName = "locationId",allocationSize = 1)
@Column(name = "id")
private Long id;

@Column(name = "course_id")
private Long courseId;

@Column(name = "location_name", nullable = true)
private String locationName;

@Column(name = "date")
private Date date;

}
