package com.example.threads;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;
import java.util.stream.IntStream;

@Slf4j
@SpringBootApplication
public class TaskExecutor {

    public static void main(String[] args) {

        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

        taskExecutor.setCorePoolSize(6);
        taskExecutor.setMaxPoolSize(100);
        taskExecutor.setQueueCapacity(50);

        /**
         * Create the BlockingQueue to use for the ThreadPoolExecutor.
         * <p>A LinkedBlockingQueue instance will be created for a positive
         * capacity value; a SynchronousQueue else.
         * @param queueCapacity the specified queue capacity
         * @return the BlockingQueue instance
         * @see java.util.concurrent.LinkedBlockingQueue
         * @see java.util.concurrent.SynchronousQueue

         *protected BlockingQueue<Runnable> createQueue(int queueCapacity) {
         *    if (queueCapacity > 0) {
         *        return new LinkedBlockingQueue<>(queueCapacity);
         *    }
         *    else {
         *        return new SynchronousQueue<>();
         *    }
         *}
        */

        taskExecutor.afterPropertiesSet();


        CyclicBarrier barrier = new CyclicBarrier(6, new Race());

        IntStream.range(0, 1000).forEach(o -> {
            taskExecutor.submit(new Car(o, barrier));
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        taskExecutor.shutdown();
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
