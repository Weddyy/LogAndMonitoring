package com.course.bff.books;

import com.course.bff.books.filter.MetricFilter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfiguration implements WebMvcConfigurer {

  @Autowired
  private MeterRegistry meterRegistry;
  @Value("${spring.application.name}")
  private String appName;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new MetricFilter(meterRegistry, appName));
  }
}
