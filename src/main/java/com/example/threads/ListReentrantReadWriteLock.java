package com.example.threads;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.IntStream;

@Slf4j
@SpringBootApplication
public class ListReentrantReadWriteLock {

    @SneakyThrows
    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(100);

        List<Integer> list = new ArrayList<>();
        List<Integer> list1 = new ArrayList<>();

        CompletableFuture<?>[] completableFutures = IntStream.range(1, 1000)
                .mapToObj(operand -> CompletableFuture.runAsync(new DbService(list, operand), executorService))
                .toArray(CompletableFuture[]::new);

        CompletableFuture<?>[] completableFutures2 = IntStream.range(1, 1000)
                .mapToObj(operand -> CompletableFuture.runAsync(new AnotherDbService(list1, operand), executorService))
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(completableFutures).thenAccept(unused -> log.info(String.valueOf(list.size())));
        CompletableFuture.allOf(completableFutures2).thenAccept(unused -> log.info(String.valueOf(list1.size())));

        executorService.shutdown();
    }

    private record DbService(List<Integer> list, int id) implements Runnable {

        @Override
        public void run() {
            list.add(id);
        }
    }

    private record AnotherDbService(List<Integer> list, int id) implements Runnable {
        static Lock writeLock = new ReentrantReadWriteLock().writeLock();

        @Override
        public void run() {
            writeLock.lock();
            try {
                list.add(id);
            } finally {
                writeLock.unlock();
            }
        }
    }

}
