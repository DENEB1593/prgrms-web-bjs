package com.github.prgrms.social.api.event.listener;

import com.github.prgrms.social.api.controller.user.UserDto;
import com.github.prgrms.social.api.event.CommentCreatedEvent;
import com.github.prgrms.social.api.message.CommentCreatedMessage;
import com.github.prgrms.social.api.model.commons.Id;
import com.github.prgrms.social.api.model.post.Post;
import com.github.prgrms.social.api.model.user.User;
import com.github.prgrms.social.api.service.post.PostService;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

public class CommentCreateEventListener implements AutoCloseable {

  @Value("${spring.kafka.topic.comment-created}")
  private String commentCreateTopic;

  private Logger logger = LoggerFactory.getLogger(CommentCreateEventListener.class);

  private final EventBus eventBus;

  private final PostService postService;

  private final KafkaTemplate<String, CommentCreatedMessage> kafkaTemplate;

  public CommentCreateEventListener(EventBus eventBus,
                                    PostService postService,
                                    KafkaTemplate<String, CommentCreatedMessage> kafkaTemplate) {
    this.eventBus = eventBus;
    this.postService = postService;
    this.kafkaTemplate = kafkaTemplate;
    eventBus.register(this);
  }

  @Subscribe
  public void handleCommentCreateEvent(CommentCreatedEvent event) throws Exception {
    Id<Post, Long> postId = event.getPostId();

    User postWriter = postService
      .findWriter(postId)
      .orElseThrow(() -> new RuntimeException("Can not find writer for " + event.getUserId()));

    CommentCreatedMessage commentCreatedMessage = new CommentCreatedMessage(new UserDto(postWriter), event);
    logger.info("Try to send push message: {}", commentCreatedMessage);
    this.kafkaTemplate.send(commentCreateTopic, postWriter.getSeq().toString(), commentCreatedMessage);
  }

  @Override
  public void close() throws Exception {
    eventBus.unregister(this);
  }

}
