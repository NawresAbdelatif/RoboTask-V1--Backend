package com.example.acwa;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@OpenAPIDefinition
@EnableCaching
@SpringBootApplication
public class AcwaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcwaApplication.class, args);
    }

}
