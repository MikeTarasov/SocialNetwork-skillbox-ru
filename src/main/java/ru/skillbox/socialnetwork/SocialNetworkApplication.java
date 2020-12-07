package ru.skillbox.socialnetwork;

import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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