package com.hanghae.week06;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class Week06beApplication {

    public static void main(String[] args) {
        SpringApplication.run(Week06beApplication.class, args);
    }

}
