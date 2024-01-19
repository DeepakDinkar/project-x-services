package com.Qomoi1.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "topics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "topicId" )
    @SequenceGenerator(name = "topicId",sequenceName = "topicId",allocationSize = 1)
    @Column(name = "topicId")
    private Long topicId;

    @Column(name = "topic_name")
    private String topicName;

    @Column(name = "topic_url")
    private String topicUrl;

}
