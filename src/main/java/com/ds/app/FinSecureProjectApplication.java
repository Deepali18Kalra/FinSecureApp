package com.ds.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.ds.app.repository")
@EntityScan(basePackages = "com.ds.app.entity")
public class FinSecureProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinSecureProjectApplication.class, args);
	
//		System.out.println(new BCryptPasswordEncoder().encode("admin123"));

	}

}
