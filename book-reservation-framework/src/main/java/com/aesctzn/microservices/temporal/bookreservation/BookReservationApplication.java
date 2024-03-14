package com.aesctzn.microservices.temporal.bookreservation;

import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.TemporalConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
public class BookReservationApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookReservationApplication.class, args);
	}

}
