package com.github.prgrms.social;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.Collections.emptyList;
import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.toList;

/**
 * CompletableFuture 설명을 위한 특별 테스트 클래스 입니다.
 */
public class CompletableFutureTest {

  private final Logger log = LoggerFactory.getLogger(getClass());

  public int 메소드_완료_1초_필요() {
    //throw new IllegalStateException("computeResult1 계산불가");
    sleep(1000L);
    return 1;
  }

  public int 메소드_완료_2초_필요() {
    sleep(2000L);
    return 2;
  }

  private void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  @Test
  void SYNC_처리는_3초_걸림() {
    long start = System.currentTimeMillis();
    int a = 메소드_완료_1초_필요();
    int b = 메소드_완료_2초_필요();
    long elapsed = System.currentTimeMillis() - start;
    log.info("[전체시간={}s] 결과는 a + b = {}", MILLISECONDS.toSeconds(elapsed), (a + b));
  }

  @Test
  void 병렬처리는_2초_걸림() {
    long start = System.currentTimeMillis();

    CompletableFuture<Integer> fut1 = supplyAsync(this::메소드_완료_1초_필요);
    CompletableFuture<Integer> fut2 = supplyAsync(this::메소드_완료_2초_필요);

    int b = fut2.join();
    log.info("fut2 걸린시간 {}s", MILLISECONDS.toSeconds((System.currentTimeMillis() - start)));

    long start2 = System.currentTimeMillis();
    int a = fut1.join();
    log.info("fut1 걸린시간 {}s", MILLISECONDS.toSeconds((System.currentTimeMillis() - start2)));

    long elapsed = System.currentTimeMillis() - start;
    log.info("[전체시간={}s] 결과는 a + b = {}", MILLISECONDS.toSeconds(elapsed), (a + b));
  }

  @Test
  void 병렬처리와_STREAM_API_조합() {
    long start = System.currentTimeMillis();
    List<CompletableFuture<Integer>> futures = new ArrayList<>();
    futures.add(supplyAsync(this::메소드_완료_1초_필요));
    futures.add(supplyAsync(this::메소드_완료_2초_필요));

    CompletableFuture<List<Integer>> results = allOf(futures.toArray(new CompletableFuture[0]))
      .thenApply(v ->
        futures.stream().map(CompletableFuture::join).collect(toList())
      )
      .exceptionally(throwable -> {
        log.error("Unexpected error occurred: {}", throwable.getMessage());
        return emptyList();
      });

    int result = results.join().stream().mapToInt(v -> v).sum();

    long elapsed = System.currentTimeMillis() - start;
    log.info("[전체시간={}s] 결과는 a + b = {}", MILLISECONDS.toSeconds(elapsed), result);
  }

}