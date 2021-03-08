package ru.skillbox.socialnetwork.config.security;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class JwtConfig {

    @Value("${jwt.token.header}")
    private String jwtHeader;

    @Value("${jwt.token.prefix}")
    private String jwtPrefix;

    @Value("${jwt.token.secret-key}")
    private String jwtSecret;

    @Value("${jwt.token.exp-time-in-min}")
    private long jwtExpTime;
}