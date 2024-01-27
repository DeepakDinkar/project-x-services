package com.Qomoi1.Service.impl;


import com.Qomoi1.Service.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTServiceImpl implements JWTService {


    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(UserDetails userDetails){
        String token = Jwts.builder().setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000)))
                .signWith(getSiginKey(), SignatureAlgorithm.HS256)
                .compact();

        return token;
    }

    public String generateRefreshToken(Map<String, Object> extractClaims, UserDetails userDetails){
        String token = Jwts.builder().setClaims(extractClaims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000)))
                .signWith(getSiginKey(), SignatureAlgorithm.HS256)
                .compact();

        return token;
    }
    public String extractUserName(String token){
        return extractClaim(token, Claims::getSubject);
    }


    private Key getSiginKey(){
        byte[] key = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(key);
    }

    private <T> T extractClaim(String token, Function<Claims,T> claimsResolvers){

        final Claims claims = extractAllClaims(token);
        return  claimsResolvers.apply(claims);
    }


    private Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSiginKey())
                .build()
                .parseClaimsJwt(token)
                .getBody();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){

        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));

    }

    private boolean isTokenExpired(String token){
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}
