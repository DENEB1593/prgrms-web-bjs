package com.github.prgrms.social.api.service.post;

import com.github.prgrms.social.api.model.commons.Id;
import com.github.prgrms.social.api.model.post.Post;
import com.github.prgrms.social.api.model.user.User;
import com.github.prgrms.social.api.repository.post.PostLikeRepository;
import com.github.prgrms.social.api.repository.post.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class PostService {

  private final PostRepository postRepository;

  private final PostLikeRepository postLikeRepository;

  public PostService(PostRepository postRepository, PostLikeRepository postLikeRepository) {
    this.postRepository = postRepository;
    this.postLikeRepository = postLikeRepository;
  }

  @Transactional
  public Post write(Post post) {
    return insert(post);
  }

  @Transactional
  public Post modify(Post post) {
    update(post);
    return post;
  }

  @Transactional
  public Optional<Post> like(Id<Post, Long> postId, Id<User, Long> writerId, Id<User, Long> userId) {
    return findById(postId, writerId, userId).map(post -> {
      if (!post.isLikesOfMe()) {
        post.incrementAndGetLikes();
        postLikeRepository.insert(userId, postId);
        update(post);
      }
      return post;
    });
  }

  @Transactional(readOnly = true)
  public Optional<Post> findById(Id<Post, Long> postId, Id<User, Long> writerId, Id<User, Long> userId) {
    checkArgument(writerId != null, "writerId must be provided.");
    checkArgument(postId != null, "postId must be provided.");
    checkArgument(userId != null, "userId must be provided.");

    return postRepository.findById(postId, writerId, userId);
  }

  @Transactional(readOnly = true)
  public List<Post> findAll(Id<User, Long> writerId, Id<User, Long> userId, long offset, int limit) {
    checkArgument(writerId != null, "writerId must be provided.");
    checkArgument(userId != null, "userId must be provided.");

    return postRepository.findAll(
      writerId,
      userId,
      checkOffset(offset),
      checkLimit(limit)
    );
  }

  @Transactional(readOnly = true)
  public Optional<User> findWriter(Id<Post, Long> postId) {
    checkNotNull(postId, "postId must be provided.");

    return postRepository.findWriter(postId);
  }

  private long checkOffset(long offset) {
    return offset < 0 ? 0 : offset;
  }

  private int checkLimit(int limit) {
    return (limit < 1 || limit > 5) ? 5 : limit;
  }

  private Post insert(Post post) {
    return postRepository.insert(post);
  }

  private void update(Post post) {
    postRepository.update(post);
  }

}