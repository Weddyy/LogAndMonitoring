package com.course.bff.books;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BeanInitializer {

  @Bean
  public RestTemplate traceAsyncRestTemplate() {
    return new RestTemplate();
  }
}
