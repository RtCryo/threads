package com.example.threads;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

@Slf4j
@SpringBootApplication
public class ThreadsCallable {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        Callable<String> callableTask = () -> {
            TimeUnit.MILLISECONDS.sleep(300);
            return "Task's execution";
        };

        List<Callable<String>> callables = new ArrayList<>();

        IntStream.range(0, 100).forEach(t -> callables.add(callableTask));

        List<Future<String>> future = executor.invokeAll(callables);

        executor.shutdown();

    }

}
