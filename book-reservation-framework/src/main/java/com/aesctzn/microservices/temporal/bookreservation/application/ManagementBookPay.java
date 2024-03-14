package com.aesctzn.microservices.temporal.bookreservation.application;


import com.aesctzn.microservices.temporal.bookreservation.domain.Reservation;
import org.springframework.http.ResponseEntity;

public interface ManagementBookPay {

    ResponseEntity<String> doPay(Reservation reservation);

}
