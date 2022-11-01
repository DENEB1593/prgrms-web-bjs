package com.github.prgrms.social.api.repository.notification;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PushMessage {

  private final String title;

  private final String clickTarget;

  private final String message;

  public PushMessage(String title, String clickTarget, String message) {
    this.title = title;
    this.clickTarget = clickTarget;
    this.message = message;
  }

  public String getTitle() {
    return title;
  }

  public String getClickTarget() {
    return clickTarget;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
      .append("title", title)
      .append("clickTarget", clickTarget)
      .append("message", message)
      .toString();
  }

}