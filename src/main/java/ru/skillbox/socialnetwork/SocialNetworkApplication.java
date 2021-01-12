package ru.skillbox.socialnetwork;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class SocialNetworkApplication {

    @Value("${db.timezone}")
    private String timeZone;


    public static void main(String[] args) {
        SpringApplication.run(SocialNetworkApplication.class, args);
    }

    @PostConstruct
    public void started() {
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
    }
}