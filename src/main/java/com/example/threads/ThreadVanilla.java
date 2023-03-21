package com.example.threads;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class ThreadVanilla {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new TestSleep());
        thread.start();
        Thread.sleep(1000);
        thread.interrupt();
    }

    private record TestSleep() implements Runnable {
        @Override
        public void run() {
            log.info("start");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {
                log.info("exception");
            }
            log.info(String.valueOf(Thread.currentThread().isInterrupted()));       //when thread sleep interrupt is false
            log.info("end");
        }
    }

}