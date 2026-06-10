package org.furb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RabbitmqWorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitmqWorkerApplication.class, args);
    }
}