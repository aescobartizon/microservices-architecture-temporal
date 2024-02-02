package com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.activities;

import com.aesctzn.microservices.temporal.bookreservation.domain.Reservation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PayReservationActivityImpl implements  PayReservationActivity {
    @Override
    public ActivityResult doPay(Reservation reservation) {
        ActivityResult activityResult = new ActivityResult();
        log.info("Pago Realizado para la reserva del libro : "+ reservation.getBook().getTitle());
        activityResult.setSummary("Pago realizado para el libro : "+ reservation.getBook().getTitle());
        return activityResult;
    }
}
