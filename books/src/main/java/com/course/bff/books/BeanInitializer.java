package com.course.bff.books;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BeanInitializer {

  @Value("${spring.application.name}")
  private String appName;

  @Bean
  public RestTemplate traceAsyncRestTemplate() {
    return new RestTemplate();
  }

  @Bean
  MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
    return registry -> registry.config().commonTags("application", appName);
  }
}
