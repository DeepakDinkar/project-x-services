package com.qomoi;

import com.qomoi.service.CronService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class QomoiApplication implements CommandLineRunner {
    private final CronService cronService;

    public QomoiApplication(CronService cronService) {
        this.cronService = cronService;
    }

    public static void main(String[] args) {
        SpringApplication.run(QomoiApplication.class, args);
    }

    public void run(String... args) {

    }

    @Scheduled(cron = "5 5 21 * * *", zone = "Asia/Dubai")
    public void deletePrevDates() {
        cronService.deletePrevDates();
    }
}