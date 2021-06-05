package com.course.bff.authors.filter;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class MetricFilter extends HandlerInterceptorAdapter {

  private static Map<String, Counter> counterMap = new ConcurrentHashMap<>();
  private static Map<String, Timer> timerMap = new ConcurrentHashMap<>();

  private final MeterRegistry meterRegistry;
  private final String appName;

  private Map<String, Long> duration = new HashMap<>();

  public MetricFilter(MeterRegistry meterRegistry, String appName) {
    this.meterRegistry = meterRegistry;
    this.appName = appName;
  }

  @Override
  public boolean preHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler) throws Exception {
    String uuid = UUID.randomUUID().toString();
    request.setAttribute("traceRequestID", uuid);
    duration.put(uuid, System.currentTimeMillis());
    return super.preHandle(request, response, handler);
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response,
                         Object handler,
                         ModelAndView modelAndView) throws Exception {
    if (handler instanceof HandlerMethod) {
      String controllerName = ((HandlerMethod) handler).getBeanType().getSimpleName();
      Counter counter = counterMap.computeIfAbsent(controllerName,
          s -> meterRegistry.counter("request_count",
              "ServiceName", appName,
              "ControllerName", controllerName));
      counter.increment();

      if (response.getStatus() != 200) {
        Counter errorCounter = counterMap.computeIfAbsent(controllerName,
            s -> meterRegistry.counter("error_count",
                "ServiceName", appName,
                "ControllerName", controllerName));
        errorCounter.increment();
      }

      String uuid = request.getAttribute("traceRequestID").toString();
      Long timeStart = duration.get(uuid);

      Timer timer = timerMap.computeIfAbsent(controllerName,
          s -> meterRegistry.timer("execution_duration",
              "ServiceName", appName,
              "ControllerName", controllerName));
      timer.record(Duration.ofMillis(System.currentTimeMillis() - timeStart));
    }

    super.postHandle(request, response, handler, modelAndView);
  }
}
