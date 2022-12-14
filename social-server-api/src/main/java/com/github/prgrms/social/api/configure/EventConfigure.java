package com.github.prgrms.social.api.configure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.prgrms.social.api.event.EventExceptionHandler;
import com.github.prgrms.social.api.event.listener.CommentCreateEventListener;
import com.github.prgrms.social.api.event.listener.JoinEventListener;
import com.github.prgrms.social.api.service.post.PostService;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@ConfigurationProperties(prefix = "eventbus")
public class EventConfigure {

  private int asyncPoolCore;
  private int asyncPoolMax;
  private int asyncPoolQueue;

  public int getAsyncPoolCore() {
    return asyncPoolCore;
  }

  public void setAsyncPoolCore(int asyncPoolCore) {
    this.asyncPoolCore = asyncPoolCore;
  }

  public int getAsyncPoolMax() {
    return asyncPoolMax;
  }

  public void setAsyncPoolMax(int asyncPoolMax) {
    this.asyncPoolMax = asyncPoolMax;
  }

  public int getAsyncPoolQueue() {
    return asyncPoolQueue;
  }

  public void setAsyncPoolQueue(int asyncPoolQueue) {
    this.asyncPoolQueue = asyncPoolQueue;
  }

  @Bean
  public TaskExecutor eventTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setThreadNamePrefix("EventBus-");
    executor.setCorePoolSize(asyncPoolCore);
    executor.setMaxPoolSize(asyncPoolMax);
    executor.setQueueCapacity(asyncPoolQueue);
    executor.afterPropertiesSet();
    return executor;
  }

  @Bean
  public EventExceptionHandler eventExceptionHandler() {
    return new EventExceptionHandler();
  }

  @Bean
  public EventBus eventBus(TaskExecutor eventTaskExecutor, EventExceptionHandler eventExceptionHandler) {
    return new AsyncEventBus(eventTaskExecutor, eventExceptionHandler);
  }

  @Bean(destroyMethod = "close")
  public JoinEventListener joinEventListener(EventBus eventBus, KafkaTemplate kafkaTemplate, ObjectMapper objectMapper) {
    return new JoinEventListener(eventBus, kafkaTemplate, objectMapper);
  }

  @Bean(destroyMethod = "close")
  public CommentCreateEventListener commentCreateEventListener(EventBus eventBus, PostService postService, KafkaTemplate kafkaTemplate) {
    return new CommentCreateEventListener(eventBus, postService, kafkaTemplate);
  }

}