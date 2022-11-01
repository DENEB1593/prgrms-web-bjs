DROP TABLE IF EXISTS connections CASCADE;
DROP TABLE IF EXISTS likes CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS posts CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- 사용자 데이터이다.
CREATE TABLE users
(
    seq           bigint      NOT NULL AUTO_INCREMENT,              -- 사용자 PK
    name          varchar(10) NOT NULL,                             -- 이름
    email         varchar(50) NOT NULL,                             -- 로그인 이메일
    passwd        varchar(80) NOT NULL,                             -- 비밀번호
    profile_image_url varchar(255) DEFAULT NULL,                    -- 이미지 주소
    login_count   int         NOT NULL DEFAULT 0,                   -- 로그인횟수
    last_login_at datetime             DEFAULT NULL,                -- 마지막 로그인일시
    create_at     datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP(), -- 생성일시
    PRIMARY KEY (seq),
    CONSTRAINT unq_user_email UNIQUE (email)
);

-- 담벼락 글(POST) 데이터이다.
-- `user_seq`는 POST 작성자를 가리킨다.
-- `comment_count`는 해당 POST의 댓글 갯수이며, comments 테이블에서 모두 찾을 수 있다.
CREATE TABLE posts
(
    seq           bigint       NOT NULL AUTO_INCREMENT,              -- POST PK
    user_seq      bigint       NOT NULL,                             -- POST 작성자 PK
    contents      varchar(500) NOT NULL,                             -- 내용
    like_count    int          NOT NULL DEFAULT 0,                   -- 좋아요수
    comment_count int          NOT NULL DEFAULT 0,                   -- 댓글수
    create_at     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP(), -- 생성일시
    PRIMARY KEY (seq),
    CONSTRAINT fk_post_to_user FOREIGN KEY (user_seq) REFERENCES users (seq) ON DELETE RESTRICT ON UPDATE RESTRICT
);

-- 특정 POST의 댓글 데이터이다.
-- `user_seq`는 댓글 작성자를 가리키고, `post_seq`는 대상 댓글을 가리킨다.
CREATE TABLE comments
(
    seq       bigint       NOT NULL AUTO_INCREMENT,              -- 댓글 PK
    user_seq  bigint       NOT NULL,                             -- 댓글 작성자 PK
    post_seq  bigint       NOT NULL,                             -- POST PK (댓글을 추가한 POST)
    contents  varchar(500) NOT NULL,                             -- 내용
    create_at datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP(), -- 생성일시
    PRIMARY KEY (seq),
    CONSTRAINT fk_comment_to_user FOREIGN KEY (user_seq) REFERENCES users (seq) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_comment_to_post FOREIGN KEY (post_seq) REFERENCES posts (seq) ON DELETE CASCADE ON UPDATE CASCADE
);

-- 사용자들의 친구 관계 데이터이다.
-- 해당 테이블의 Row 데이터는 `user_seq`, `target_seq`가 친구 관계임을 의미한다.
-- 단, `user_seq` -> `target_seq` 방향으로만 친구관계가 되며, 역방향 `target_seq` -> `user_seq`은 친구가 아니다.
-- ("`target_seq`는 `user_seq`의 친구이다"는 성립하지만, "`user_seq`는 `target_seq`의 친구이다"는 성립하지 않는다.)
CREATE TABLE connections
(
    seq        bigint   NOT NULL AUTO_INCREMENT,              -- 친구관계 PK
    user_seq   bigint   NOT NULL,                             -- 사용자 PK
    target_seq bigint   NOT NULL,                             -- 친구 사용자 PK
    granted_at datetime          DEFAULT NULL,                -- 친구수락일시
    create_at  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP(), -- 생성일시
    PRIMARY KEY (seq),
    CONSTRAINT fk_connection_to_user FOREIGN KEY (user_seq) REFERENCES users (seq) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_connection_to_user2 FOREIGN KEY (target_seq) REFERENCES users (seq) ON DELETE RESTRICT ON UPDATE RESTRICT
);

-- POST 좋아요 여부
-- 해당 테이블의 Row 데이터는 `user_seq` 가리키는 사용자가 `post_seq` 가리키는 POST에 대해 좋아요했음을 의미한다.
CREATE TABLE likes
(
    seq       bigint   NOT NULL AUTO_INCREMENT,              -- 좋아요 PK
    user_seq  bigint   NOT NULL,                             -- 사용자 PK
    post_seq  bigint   NOT NULL,                             -- POST PK (좋아요를 누른 POST)
    create_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP(), -- 생성일시
    PRIMARY KEY (seq),
    CONSTRAINT unq_likes_user_post UNIQUE (user_seq, post_seq),
    CONSTRAINT fk_likes_to_user FOREIGN KEY (user_seq) REFERENCES users (seq) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_likes_to_post FOREIGN KEY (post_seq) REFERENCES posts (seq) ON DELETE CASCADE ON UPDATE CASCADE
);

-- PUSH Notification 구독데이터
CREATE TABLE subscriptions
(
    seq        bigint       NOT NULL AUTO_INCREMENT,
    user_seq   bigint       NOT NULL,
    endpoint   varchar(200) NOT NULL,
    public_key varchar(200) NOT NULL,
    auth       varchar(200) NOT NULL,
    create_at  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    CONSTRAINT unq_subscriptions UNIQUE (user_seq, endpoint),
    CONSTRAINT fk_subscriptions_to_user FOREIGN KEY (user_seq) REFERENCES users (seq) ON DELETE RESTRICT ON UPDATE RESTRICT
);
