package com.example.threads;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootApplication
public class ThreadsPhaser {

    public static void main(String[] args) {
        Phaser phaser = new Phaser(1);
        List<Passenger> passengers = new ArrayList<>();
        passengers.add(new Passenger(1, 1, 3, phaser));
        passengers.add(new Passenger(2, 1, 5, phaser));
        passengers.add(new Passenger(3, 1, 2, phaser));
        passengers.add(new Passenger(4, 3, 5, phaser));
        passengers.add(new Passenger(5, 2, 4, phaser));

        log.info("the bus leaves");
        for (int i = 1; i < 6; i++) {
            for (Passenger p: passengers) {
                if (p.departure == phaser.getPhase()) {
                    phaser.register();
                    new Thread(p).start();
                }
            }
            log.info("opens the door on the " + phaser.getPhase() + " station");
            phaser.arriveAndAwaitAdvance();
            log.info("close the door");
        }
        log.info("the bus arrived at the terminal");
        phaser.arriveAndDeregister();

    }

    private record Passenger(int id, int departure, int destination, Phaser phaser) implements Runnable {

        private Passenger {
            log.info("Passenger {" + departure + " -> " + destination + "} " + id + " waiting");
        }

        @SneakyThrows
        @Override
        public void run() {
            log.info("Passenger " + id + " enter the bus");
            while (phaser.getPhase() < destination) {
                phaser.arriveAndAwaitAdvance();
            }
            TimeUnit.SECONDS.sleep(1);
            log.info("Passenger " + id + " left the bus");
            phaser.arriveAndDeregister();
        }
    }

}
