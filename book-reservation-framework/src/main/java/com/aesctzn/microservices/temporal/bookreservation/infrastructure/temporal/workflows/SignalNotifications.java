package com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.workflows;

import com.aesctzn.microservices.temporal.bookreservation.domain.Book;
import com.aesctzn.microservices.temporal.bookreservation.domain.Reservation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignalNotifications {

    public SignalNotifications(){

    }

    private boolean sendNotification = false;

    private String destinatario = "";

    private String seviceName = "";

    private Reservation reservation;
}
