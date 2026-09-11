package com.uniq.placement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PlacementBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlacementBackendApplication.class, args);
    }
}
