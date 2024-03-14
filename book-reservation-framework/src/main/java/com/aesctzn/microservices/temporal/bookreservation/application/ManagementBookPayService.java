package com.aesctzn.microservices.temporal.bookreservation.application;

import com.aesctzn.microservices.temporal.bookreservation.application.ManagementBookPay;
import com.aesctzn.microservices.temporal.bookreservation.domain.Reservation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ManagementBookPayService implements ManagementBookPay {
    @Override
    public ResponseEntity<String> doPay(Reservation reservation) {
        return null;
    }
}
