package com.chumzclone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ChumzCloneApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChumzCloneApplication.class, args);
    }
}
