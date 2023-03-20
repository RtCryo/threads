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

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        List<Runnable> threads = new ArrayList<>();

        for (int t = 0; t < 10; t++)
            threads.add(new Worker(countDownLatch));

        threads.forEach(runnable -> CompletableFuture.runAsync(runnable, executorService).thenAccept(unused -> log.info("exit")));

        log.info("main exit");
        executorService.shutdown();

    }


}


@Slf4j
class Worker implements Runnable {

    private final CountDownLatch latch;

    public Worker(final CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            log.info("do important job, latch: " + latch.getCount());
            TimeUnit.SECONDS.sleep(1);
            latch.countDown();
            log.info("job successful");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}