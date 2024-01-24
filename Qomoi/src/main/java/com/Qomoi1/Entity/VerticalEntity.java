package com.Qomoi1.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "verticals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerticalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "verticalId" )
    @SequenceGenerator(name = "verticalId",sequenceName = "verticalId",allocationSize = 1)
    @Column(name = "verticalId")
    private Long verticalId;

    @Column(name = "vertical_name")
    private String verticalName;

    @Column(name = "vertical_url")
    private String verticalUrl;

}
