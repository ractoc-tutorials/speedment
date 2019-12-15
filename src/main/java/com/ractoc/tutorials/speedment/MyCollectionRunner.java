package com.ractoc.tutorials.speedment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(
        exclude = SecurityAutoConfiguration.class,
        scanBasePackages = {"com.ractoc.tutorials.speedment"})
public class MyCollectionRunner {
    public static void main(String[] args) {
        SpringApplication.run(MyCollectionRunner.class, args);
    }
}
