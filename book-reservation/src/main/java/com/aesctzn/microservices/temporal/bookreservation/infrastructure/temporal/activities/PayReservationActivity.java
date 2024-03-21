package com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.activities;

import com.aesctzn.microservices.temporal.bookreservation.domain.Book;
import com.aesctzn.microservices.temporal.bookreservation.domain.Reservation;
import io.temporal.activity.ActivityExecutionContext;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface PayReservationActivity {
    @ActivityMethod
    ActivityResult doPay(Reservation reservation);

    @ActivityMethod
    void compensatePay(Book book);
}
