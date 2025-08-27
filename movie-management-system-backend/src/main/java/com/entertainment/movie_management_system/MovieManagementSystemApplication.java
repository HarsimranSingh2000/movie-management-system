package com.entertainment.movie_management_system;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MovieManagementSystemApplication {

	public static void main(String[] args) {
		// Force JVM default timezone to avoid PostgreSQL rejecting legacy IDs like Asia/Calcutta
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
		System.setProperty("user.timezone", "Asia/Kolkata");
		SpringApplication.run(MovieManagementSystemApplication.class, args);
	}

}
