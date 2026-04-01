package com.ds.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling   // ← for @Scheduled salary job
@EnableAsync        // ← for @Async email sending

public class FinSecureProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinSecureProjectApplication.class, args);
	}
}
