package com.example.threads;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

@Slf4j
@SpringBootApplication
public class ThreadsCyclicBarrier {

    public static void main(String[] args) {

        //ExecutorService executorService = Executors.newFixedThreadPool(12);

        ExecutorService executorService = new ThreadPoolExecutor(4, 100,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(50));

        CyclicBarrier barrier = new CyclicBarrier(4, new Race());

        List<Car> threads = new ArrayList<>();

        IntStream.range(0, 1000).forEach(o -> threads.add(new Car(o, barrier)));

        threads.forEach(car -> {
            executorService.submit(car);
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        executorService.shutdown();

    }

    private record Race() implements Runnable {

        @Override
        public void run() {
            log.info("5..4..3..2..1..Start!");
        }
    }

    private record Car(int carNumber, CyclicBarrier barrier) implements Runnable {

        @SneakyThrows
        @Override
        public void run() {
            log.info("car " + carNumber + " is ready!");
            barrier.await();
            TimeUnit.MILLISECONDS.sleep(800);
            log.info("car " + carNumber + " finished!");
        }
    }
}