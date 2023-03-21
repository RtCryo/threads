package com.example.threads;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

@Slf4j
@SpringBootApplication
public class ThreadsApplication {

    private static final Collection<Double> result = Collections.synchronizedCollection(new ArrayList<>());

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Supplier<Double>> threads = new ArrayList<>();

        for (int t = 0; t < 100; t++) {
            int finalT = t;
            threads.add(() -> thread(finalT));
        }

        CompletableFuture<?>[] futures = threads.stream()
                .map(o ->  CompletableFuture.supplyAsync(o, executorService).thenAccept(ThreadsApplication::addDouble))
                .toArray(CompletableFuture[]::new);

        executorService.shutdown();

        log.info(String.valueOf(CompletableFuture.allOf(futures).isDone()));

        CompletableFuture.allOf(futures)
                .thenRun(() -> log.info("result:"))
                .thenRun(() -> result.forEach(d -> log.info(String.valueOf(d))))
                .thenRun(() -> log.info(String.valueOf(thread(5))));

        log.info(String.valueOf(CompletableFuture.allOf(futures).isDone()));

        log.info("main exit");

    }

    public static double thread(int num) {
        log.info("start: " +num);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("exit");
        return Math.random() * 100 / num;
    }

    private static synchronized void addDouble(double d) {
        result.add(d);
    }

}
