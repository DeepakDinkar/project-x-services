package com.Qomoi.Qomoi;


import com.Qomoi.Qomoi.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@SpringBootApplication
public class QomoiApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(QomoiApplication.class, args);
	}

	public void run(String... args) {

	}
}