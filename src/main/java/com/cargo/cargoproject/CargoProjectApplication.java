package com.cargo.cargoproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.cargo")
@EnableJpaRepositories(basePackages = "com.cargo.repository")   // veya "com.cargo.repositories"
@EntityScan(basePackages = "com.cargo.models.entities")         // Entitylerin gerçek paketi
public class CargoProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(CargoProjectApplication.class, args);
    }

}
