package ru.skillbox.socialnetwork.config;

import javax.servlet.MultipartConfigElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;



@Configuration
@EnableAutoConfiguration
public class MultipartFileConfig {

  @Value("${upload.max.file.size}")
  private int maxFileSizeInMb;

  @Bean
  MultipartConfigElement multipartConfigElement() {
    MultipartConfigFactory factory = new MultipartConfigFactory();
    factory.setMaxFileSize(DataSize.ofMegabytes(maxFileSizeInMb));
    return factory.createMultipartConfig();
  }

}
