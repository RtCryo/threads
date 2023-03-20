package com.example.threads;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@SpringBootApplication
public class ThreadSchedulerApplication {

    private static final AtomicInteger count = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        ScheduledExecutorService ses = Executors.newScheduledThreadPool(10);

        ScheduledFuture<?> future = ses.scheduleAtFixedRate(ThreadSchedulerApplication::additionCount, 0,10, TimeUnit.MILLISECONDS);

        while (true) {
            log.info("count: " + count);
            Thread.sleep(1000);
            if (count.get() > 100) {
                future.cancel(true);
                ses.shutdown();
                break;
            }
        }

    }

    private static void additionCount() {
        log.info("increment");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        count.incrementAndGet();
    }

}
