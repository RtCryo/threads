package com.example.threads;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

@Slf4j
@SpringBootApplication
public class CounterReentrantLock {

    @SneakyThrows
    public static void main(String[] args) {

        Counter counter = new Counter();
        Counter counter2 = new Counter();

        CompletableFuture<?>[] completableFutures = IntStream.range(1, 10000)
                .mapToObj(operand -> CompletableFuture.runAsync(new CounterThread(counter))).toArray(CompletableFuture[]::new);

        CompletableFuture<?>[] completableFutures2 = IntStream.range(1, 10000)
                .mapToObj(operand -> CompletableFuture.runAsync(new CounterLockThread(counter2))).toArray(CompletableFuture[]::new);


        CompletableFuture.allOf(completableFutures).thenRun(() -> log.info(String.valueOf(counter.getCount())));
        CompletableFuture.allOf(completableFutures2).thenRun(() -> log.info(String.valueOf(counter2.getCount())));
    }

    record CounterThread(Counter counter) implements Runnable {
        @Override
        public void run() {
            counter.inc();
        }
    }

    record CounterLockThread(Counter counter) implements Runnable {

        static Lock lock = new ReentrantLock();

        @Override
        public void run() {
            lock.lock();
            try {
                counter.inc();
            } finally {
                lock.unlock();
            }
        }
    }


    private static class Counter {
        private int count = 0;

        public void inc() {
            count++;
        }

        public int getCount() {
            return count;
        }
    }

}

