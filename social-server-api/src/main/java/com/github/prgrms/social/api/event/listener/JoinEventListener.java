package com.github.prgrms.social.api.event.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.prgrms.social.api.event.UserJoinedEvent;
import com.github.prgrms.social.api.message.UserJoinedMessage;
import com.github.prgrms.social.api.model.commons.Id;
import com.github.prgrms.social.api.model.user.User;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

public class JoinEventListener implements AutoCloseable {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Value("${spring.kafka.topic.user-joined}")
  private String userJoinedTopic;

  private final EventBus eventBus;

  private final KafkaTemplate<String, UserJoinedMessage> kafkaTemplate;

  private final ObjectMapper objectMapper;

  public JoinEventListener(EventBus eventBus, KafkaTemplate<String, UserJoinedMessage> kafkaTemplate, ObjectMapper objectMapper) {
    this.eventBus = eventBus;
    this.kafkaTemplate = kafkaTemplate;
    this.objectMapper = objectMapper;

    eventBus.register(this);
  }

  @Subscribe
  public void handleJoinEvent(UserJoinedEvent event) {
    String name = event.getName();
    Id<User, Long> userId = event.getUserId();
    log.info("user {}, userId {} joined!", name, userId);

    this.kafkaTemplate.send(userJoinedTopic, new UserJoinedMessage(event));
  }

  @Override
  public void close() throws Exception {
    eventBus.unregister(this);
  }

}