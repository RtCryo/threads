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

    public static void main(String[] args) throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        List<Supplier<Double>> threads = new ArrayList<>();

        for (int t = 0; t < 1000; t++) {
            int finalT = t;
            threads.add(() -> thread(finalT));
        }
//
//        CompletableFuture<?>[] futures = threads.stream()
//                .map(task -> CompletableFuture.supplyAsync(() -> task, executorService).thenAccept(runnable -> System.out.println(runnable.toString())))
//                .toArray(CompletableFuture[]::new);
//
//        executorService.shutdown();
//
//        System.out.println("await");
//        CompletableFuture.allOf(futures).join();
//        System.out.println("done");


        CompletableFuture<?>[] futures = threads.stream()
                .map(o ->  CompletableFuture.supplyAsync(o, executorService).thenAccept(ThreadsApplication::addDouble))
                .toArray(CompletableFuture[]::new);

        executorService.shutdown();

        log.info(String.valueOf(CompletableFuture.allOf(futures).isDone()));

        CompletableFuture.allOf(futures)
                .thenRun(() -> log.info("done"))
                .thenRun(() -> log.info(String.valueOf(thread(5))));

        log.info(String.valueOf(CompletableFuture.allOf(futures).isDone()));

        result.forEach(aDouble -> log.info(String.valueOf(aDouble)));



        //CompletableFuture.supplyAsync(() -> thread(10)).thenAccept(aDouble -> log.info(String.valueOf(aDouble)));


    }

    public static double thread(int num) {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return Math.random() * 100 / num;
    }

    private static synchronized void addDouble(double d) {
        result.add(d);
    }

}
