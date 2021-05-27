package com.lohika.course.bfffrontend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class BeanInitializer {


  @Value("${books.url}")
  private String booksUrl;

  @Value("${authors.url}")
  private String authorsUrl;

  @Bean
  public WebClient authorClient() {
    return WebClient
        .builder()
        .baseUrl(authorsUrl)
        .build();
  }

  @Bean
  public WebClient bookClient() {
    return WebClient
        .builder()
        .baseUrl(booksUrl)
        .build();
  }
}
