package com.qomoi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "qomoi_users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDE {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
    @SequenceGenerator(name = "user_generator", sequenceName = "user_s", allocationSize = 1)
    private long userId;

    @Column(name = "lastName")
    private String lastName;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "mobile", unique = true)
    private String mobile;

    @Column(name = "emailId", unique = true)
    private String emailId;

    @Column(name = "userType")
    private String userType;

    @Column(name = "password")
    private String password;

    @Column(name = "status")
    private String status;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "address1")
    private String address1;

    @Column(name = "address2")
    private String address2;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "zipcode")
    private String zipcode;

    @Column(name = "is_facebook")
    private Boolean isFacebook;

    @Column(name = "is_google")
    private Boolean isGoogle;

    @Column(name = "is_normal")
    private Boolean isNormal;

    @Column(name = "salt")
    private String salt;

    @Column(name = "profile_image")
    private String profileImage;

}
