package com.github.prgrms.social.api.message;

import com.github.prgrms.social.api.event.UserJoinedEvent;

public class UserJoinedMessage {

  private Long userId;

  private String name;

  public UserJoinedMessage() {
  }

  public UserJoinedMessage(UserJoinedEvent event) {
    this.userId = event.getUserId().value();
    this.name = event.getName();
  }

  public Long getUserId() {
    return userId;
  }

  public String getName() {
    return name;
  }

}
