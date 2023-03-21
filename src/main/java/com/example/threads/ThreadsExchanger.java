package com.example.threads;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootApplication
public class ThreadsExchanger {

    public static void main(String[] args) throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        List<Trans> trans1 = new ArrayList<>();
        List<Trans> trans2 = new ArrayList<>();
        trans1.add(new Boat());
        trans1.add(new Car());
        trans2.add(new Car());
        trans2.add(new Boat());
        Exchanger<Trans> exchanger = new Exchanger<>();

        executorService.submit(new Trip(exchanger, trans1));
        TimeUnit.SECONDS.sleep(1);
        executorService.submit(new Trip(exchanger, trans2));

        executorService.shutdown();
    }


    @Slf4j
    private record Trip(Exchanger<Trans> exchanger, List<Trans> transList) implements Runnable {

        @Override
        public void run() {
            log.info("Trip is ready!");
            transList.forEach(trans -> log.info(Thread.currentThread().getName() + " have " + trans.toString()));
            log.info("exchange!");
            try {
                transList.set(1, exchanger.exchange(transList.get(1)));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("exchanged!");
            transList.forEach(trans -> log.info(Thread.currentThread().getName() + " have now " + trans.toString()));
        }
    }

    private record Boat() implements Trans {}

    private  record Car() implements Trans {}

    private interface Trans {}

}