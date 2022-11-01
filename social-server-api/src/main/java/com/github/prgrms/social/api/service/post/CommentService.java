package com.github.prgrms.social.api.service.post;

import com.github.prgrms.social.api.error.NotFoundException;
import com.github.prgrms.social.api.event.CommentCreatedEvent;
import com.github.prgrms.social.api.model.commons.Id;
import com.github.prgrms.social.api.model.post.Comment;
import com.github.prgrms.social.api.model.post.Post;
import com.github.prgrms.social.api.model.user.User;
import com.github.prgrms.social.api.repository.post.CommentRepository;
import com.github.prgrms.social.api.repository.post.PostRepository;
import com.google.common.eventbus.EventBus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.emptyList;

@Service
public class CommentService {

  private final EventBus eventBus;

  private final PostRepository postRepository;

  private final CommentRepository commentRepository;

  public CommentService(EventBus eventBus, PostRepository postRepository, CommentRepository commentRepository) {
    this.eventBus = eventBus;
    this.postRepository = postRepository;
    this.commentRepository = commentRepository;
  }

  @Transactional
  public Comment write(Id<Post, Long> postId, Id<User, Long> postWriterId, Id<User, Long> userId, Comment comment) {
    checkArgument(comment != null, "comment must be provided.");
    checkArgument(comment.getPostId().equals(postId), "comment.postId must equals postId");
    checkArgument(comment.getUserId().equals(userId), "comment.userId must equals userId");

    return findPost(postId, postWriterId, userId)
      .map(post -> {
        post.incrementAndGetComments();
        postRepository.update(post);

        Comment inserted = insert(comment);
        // raise event
        eventBus.post(new CommentCreatedEvent(inserted));
        return inserted;
      })
      .orElseThrow(() -> new NotFoundException(Post.class, postId, userId));
  }

  @Transactional(readOnly = true)
  public List<Comment> findAll(Id<Post, Long> postId, Id<User, Long> postWriterId, Id<User, Long> userId) {
    return findPost(postId, postWriterId, userId)
      .map(post -> commentRepository.findAll(postId))
      .orElse(emptyList());
  }

  private Optional<Post> findPost(Id<Post, Long> postId, Id<User, Long> postWriterId, Id<User, Long> userId) {
    checkArgument(postId != null, "postId must be provided.");
    checkArgument(postWriterId != null, "postWriterId must be provided.");
    checkArgument(userId != null, "userId must be provided.");

    return postRepository.findById(postId, postWriterId, userId);
  }

  private Comment insert(Comment comment) {
    return commentRepository.insert(comment);
  }

  private void update(Comment comment) {
    commentRepository.update(comment);
  }

}