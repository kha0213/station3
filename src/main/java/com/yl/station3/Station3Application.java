package com.yl.station3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class Station3Application {

    public static void main(String[] args) {
        SpringApplication.run(Station3Application.class, args);
    }

}
