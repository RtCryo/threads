package com.example.threads;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@SpringBootApplication
public class Conditions {

    @SneakyThrows
    public static void main(String[] args) {

        List<String> goods = new ArrayList<>();
        Store store = new Store(goods);

        Thread producer = new Thread(new Producer(store));
        Thread consumer = new Thread(new Consumer(store));

        producer.start();
        consumer.start();

        while (consumer.isAlive() || producer.isAlive()) {
            TimeUnit.SECONDS.sleep(1);
        }

        log.info(String.valueOf(goods.size()));

    }

    private record Producer(Store store) implements Runnable {
        private static final String[] goods = {"Milk", "Sugar", "Toast", "Potatoes", "Onion"};

        @SneakyThrows
        @Override
        public void run() {
            for (String i : goods) {
                log.info("put new product: " + i);
                TimeUnit.MILLISECONDS.sleep(100);
                store.put(i);
                log.info("new product is available");
                log.info("goods: " + store.goods().size());
            }
        }
    }

    private record Consumer(Store store) implements Runnable {

        @SneakyThrows
        @Override
        public void run() {
            for (int i = 0; i < 5; i++) {
                log.info("Get product");
                TimeUnit.SECONDS.sleep(2);
                store.get();
                log.info("Product removed");
            }
        }
    }

    private record Store(List<String> goods) {
        private static final ReentrantLock lock = new ReentrantLock();
        private static final Condition conditions = lock.newCondition();

        @SneakyThrows
        public void put(final String product) {
            lock.lock();
            try {
                while (goods.size() > 3) {
                    log.info("Store have too many products - await");
                    conditions.await();
                }
                goods.add(product);
                conditions.signalAll();
            } finally {
                lock.unlock();
            }
        }

        @SneakyThrows
        public void get() {
            lock.lock();
            try {
                while (goods.isEmpty()) {
                    log.info("Store have no many products - await");
                    conditions.await();
                }
                goods.remove(0);
                conditions.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }


}
