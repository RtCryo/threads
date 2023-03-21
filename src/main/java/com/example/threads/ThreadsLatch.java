package com.example.threads;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@SpringBootApplication
public class ThreadsLatch {
    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(5);

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        List<Runnable> threads = new ArrayList<>();

        for (int t = 0; t < 10; t++)
            threads.add(new Worker(countDownLatch));

        threads.forEach(runnable -> CompletableFuture.runAsync(runnable, executorService).thenAccept(unused -> log.info("exit")));

        log.info("main exit");
        executorService.shutdown();

    }

    private record Worker(CountDownLatch latch) implements Runnable {

        @Override
        public void run() {
            try {
                log.info("do important job, latch: " + latch.getCount());
                TimeUnit.SECONDS.sleep(1);
                latch.countDown();
                latch.await();
                log.info("job successful");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
