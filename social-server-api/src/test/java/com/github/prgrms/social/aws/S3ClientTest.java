package com.github.prgrms.social.aws;

import com.github.prgrms.social.api.aws.S3Client;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.net.URL;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class S3ClientTest {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private S3Client s3Client;

  @Autowired
  public void setS3Client(S3Client s3Client) {
    this.s3Client = s3Client;
  }

  @Test
  void S3_버킷으로_이미지를_업로드_및_삭제_할수있다() {
    URL testProfile = getClass().getResource("/test_profile.jpg");
    assertThat(testProfile, is(notNullValue()));

    File file = new File(testProfile.getFile());
    String url = s3Client.upload(file);
    assertThat(url, is(notNullValue()));
    log.info("S3 bucket url: {}", url);

    s3Client.delete(url);
  }

}