package com.aesctzn.microservices.temporal.bookreservation.application;

import com.aesctzn.microservices.temporal.bookreservation.domain.Reservation;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.workflows.SignalNotifications;

public interface Reservations {

    void doReservation(Reservation reservations);

    void sendNotification(SignalNotifications notification);

    Reservation getReservationInfo(String bookTitle);
}
