package com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.workflows.childs;

import com.aesctzn.microservices.temporal.bookreservation.domain.Reservation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReservationProcessActivityImpl implements  ReservationProcessActivity {
    @Override
    public String processReservation(Reservation reservation) {
        log.info("Reserva Procesada por la actividad : "+reservation.getBook().getTitle());
        return "Reserva Procesada por la actividad : "+reservation.getBook().getTitle();
    }
}
