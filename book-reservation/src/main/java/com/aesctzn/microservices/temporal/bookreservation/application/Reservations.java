package com.aesctzn.microservices.temporal.bookreservation.application;

import com.aesctzn.microservices.temporal.bookreservation.domain.Reservation;

public interface Reservations {

    void doReservation(Reservation reservations);
}
