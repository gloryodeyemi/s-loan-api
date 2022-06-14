package com.example.sloan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SLoanApplication {

    public static void main(String[] args) {
        SpringApplication.run(SLoanApplication.class, args);
    }

}
