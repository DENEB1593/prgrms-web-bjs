package com.github.prgrms.social.api.event;

import com.github.prgrms.social.api.model.commons.Id;
import com.github.prgrms.social.api.model.user.User;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class UserJoinedEvent {

  private final Id<User, Long> userId;

  private final String name;

  public UserJoinedEvent(User user) {
    this.userId = Id.of(User.class, user.getSeq());
    this.name = user.getName();
  }

  public Id<User, Long> getUserId() {
    return userId;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
      .append("userId", userId)
      .append("name", name)
      .toString();
  }

}
