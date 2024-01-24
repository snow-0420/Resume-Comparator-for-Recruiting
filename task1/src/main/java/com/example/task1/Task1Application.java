package com.example.task1;

import com.example.task1.service.StorageProperties;
import com.example.task1.service.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class Task1Application {

	public static void main(String[] args) {
		SpringApplication.run(Task1Application.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return args -> {
			storageService.deleteAll();
			storageService.init();
		};
	}
}
