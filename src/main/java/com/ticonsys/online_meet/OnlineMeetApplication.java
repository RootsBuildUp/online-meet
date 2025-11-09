package com.ticonsys.online_meet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OnlineMeetApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineMeetApplication.class, args);
		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "INFO");

	}

}
