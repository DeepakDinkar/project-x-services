package com.qomoi;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication

public class QomoiApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(QomoiApplication.class, args);
	}

	public void run(String... args) {

	}

	@Scheduled(cron = "5 5 0 * * *", zone = "GST")
	public void runEngTasks() {
//        System.out.println("Scheduled EngTasks task running");

	}
}