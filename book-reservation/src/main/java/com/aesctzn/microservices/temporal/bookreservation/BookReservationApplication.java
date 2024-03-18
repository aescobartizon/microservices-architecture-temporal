package com.aesctzn.microservices.temporal.bookreservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BookReservationApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookReservationApplication.class, args);
	}

}
