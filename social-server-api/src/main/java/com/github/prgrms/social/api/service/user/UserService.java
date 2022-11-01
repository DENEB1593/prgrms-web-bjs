package com.github.prgrms.social.api.service.user;

import com.github.prgrms.social.api.error.NotFoundException;
import com.github.prgrms.social.api.event.UserJoinedEvent;
import com.github.prgrms.social.api.model.commons.Id;
import com.github.prgrms.social.api.model.user.ConnectedUser;
import com.github.prgrms.social.api.model.user.Email;
import com.github.prgrms.social.api.model.user.User;
import com.github.prgrms.social.api.repository.user.UserRepository;
import com.google.common.eventbus.EventBus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Service
public class UserService {

  private final PasswordEncoder passwordEncoder;

  private final UserRepository userRepository;

  private final EventBus eventBus;

  public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, EventBus eventBus) {
    this.passwordEncoder = passwordEncoder;
    this.userRepository = userRepository;
    this.eventBus = eventBus;
  }

  @Transactional
  public User join(String name, Email email, String password) {
    checkArgument(isNotEmpty(password), "password must be provided.");
    checkArgument(
      password.length() >= 4 && password.length() <= 15,
      "password length must be between 4 and 15 characters."
    );

    User user = new User(name, email, passwordEncoder.encode(password));
    User saved = insert(user);

    // raise event
    eventBus.post(new UserJoinedEvent(saved));
    return saved;
  }

  @Transactional
  public User login(Email email, String password) {
    checkArgument(password != null, "password must be provided.");

    User user = findByEmail(email)
      .orElseThrow(() -> new NotFoundException(User.class, email));
    user.login(passwordEncoder, password);
    user.afterLoginSuccess();
    update(user);
    return user;
  }

  @Transactional
  public User updateProfileImage(Id<User, Long> userId, String profileImageUrl) {
    User user = findById(userId)
      .orElseThrow(() -> new NotFoundException(User.class, userId));
    user.updateProfileImage(profileImageUrl);
    update(user);
    return user;
  }

  @Transactional(readOnly = true)
  public Optional<User> findById(Id<User, Long> userId) {
    checkArgument(userId != null, "userId must be provided.");

    return userRepository.findById(userId);
  }

  @Transactional(readOnly = true)
  public Optional<User> findByEmail(Email email) {
    checkArgument(email != null, "email must be provided.");

    return userRepository.findByEmail(email);
  }

  @Transactional(readOnly = true)
  public List<ConnectedUser> findAllConnectedUser(Id<User, Long> userId) {
    checkArgument(userId != null, "userId must be provided.");

    return userRepository.findAllConnectedUser(userId);
  }

  @Transactional(readOnly = true)
  public List<Id<User, Long>> findConnectedIds(Id<User, Long> userId) {
    checkArgument(userId != null, "userId must be provided.");

    return userRepository.findConnectedIds(userId);
  }

  private User insert(User user) {
    return userRepository.insert(user);
  }

  private void update(User user) {
    userRepository.update(user);
  }

}