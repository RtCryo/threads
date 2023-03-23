package com.example.threads;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

@Slf4j
@SpringBootApplication
public class ThreadsStampedLock {

    private int count = 0;
    private final StampedLock stampedLock = new StampedLock();

    public static void main(String[] args) {
        new ThreadsStampedLock().go();
    }

    @SneakyThrows
    private void go() {
        Thread thread1 = new Thread(this::counterReader);
        Thread thread2 = new Thread(this::counterReader);
        Thread thread3 = new Thread(this::counterWriter);

        thread1.start();
        thread2.start();
        thread3.start();

        thread1.join();
        thread2.join();
        thread3.join();
    }

    @SneakyThrows
    private void counterReader() {
        while (!Thread.currentThread().isInterrupted()) {
            long stamp = stampedLock.tryOptimisticRead();
            int temp = count;
            if(!stampedLock.validate(stamp)) {
                log.info("counter has been changed, read lock is on");
                stamp = stampedLock.readLock();
                try {
                    temp = count;
                } finally {
                    stampedLock.unlockRead(stamp);
                }
            }
            log.info("count: " + temp);
            TimeUnit.MILLISECONDS.sleep(1000);
        }
    }

    @SneakyThrows
    private void counterWriter() {
        while (!Thread.currentThread().isInterrupted()) {
            long stamp = stampedLock.writeLock();
            try {
                log.info("start counter modification: " + stamp);
                TimeUnit.SECONDS.sleep(4);
                int temp = count;
                temp++;
                count = temp;
            } finally {
                stampedLock.unlockWrite(stamp);
            }
            TimeUnit.SECONDS.sleep(6);
        }
    }

}
