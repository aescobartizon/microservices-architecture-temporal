package com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.workflows.childs;

import com.aesctzn.microservices.temporal.bookreservation.domain.Reservation;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.activities.ActivityResult;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface ReservationProcessActivity {

    @ActivityMethod
    String processReservation(Reservation reservation);
}
