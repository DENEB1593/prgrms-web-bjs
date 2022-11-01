package com.github.prgrms.social.api.model.user;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;

import static com.google.common.base.Preconditions.checkArgument;

public class ConnectedUser {

  private final Long seq;

  private final String name;

  private final Email email;

  // TODO profileImageUrl 추가

  private final LocalDateTime grantedAt;

  public ConnectedUser(Long seq, String name, Email email, LocalDateTime grantedAt) {
    checkArgument(seq != null, "seq must be provided.");
    checkArgument(name != null, "name must be provided.");
    checkArgument(email != null, "email must be provided.");
    checkArgument(grantedAt != null, "grantedAt must be provided.");

    this.seq = seq;
    this.name = name;
    this.email = email;
    this.grantedAt = grantedAt;
  }

  public Long getSeq() {
    return seq;
  }

  public String getName() {
    return name;
  }

  public Email getEmail() {
    return email;
  }

  public LocalDateTime getGrantedAt() {
    return grantedAt;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
      .append("seq", seq)
      .append("name", name)
      .append("email", email)
      .append("grantedAt", grantedAt)
      .toString();
  }

}