package com.qomoi.dto;

import java.io.Serializable;

public class JwtResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    private final String jwttoken;
    public String getUserName() {
        return userName;
    }

    private final String userName;

    public JwtResponse(String jwttoken, String userName) {
        this.jwttoken = jwttoken;
        this.userName = userName;
    }

    public String getToken() {
        return this.jwttoken;
    }
}