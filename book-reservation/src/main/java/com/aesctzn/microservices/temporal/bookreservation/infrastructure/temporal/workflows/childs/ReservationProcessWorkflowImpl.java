package com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.workflows.childs;

import com.aesctzn.microservices.temporal.bookreservation.domain.Reservation;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
public class ReservationProcessWorkflowImpl implements ReservationProcessWorkflow {

    private final ReservationProcessActivity reservationProcessActivity =
            Workflow.newActivityStub(
                    ReservationProcessActivity.class,
                    ActivityOptions.newBuilder()
                            .setStartToCloseTimeout(Duration.ofSeconds(60))
                            .setScheduleToCloseTimeout(Duration.ofSeconds(60))
                            .setScheduleToStartTimeout(Duration.ofSeconds(15))
                            .setRetryOptions(RetryOptions.newBuilder()
                                    .setInitialInterval(Duration.ofSeconds(5)) // Intervalo inicial entre reintentos
                                    .setMaximumAttempts(3) // Número máximo de reintentos
                                    .setDoNotRetry(String.valueOf(IllegalArgumentException.class)) // No volver a intentar para excepciones específicas
                                    .build())
                            .setHeartbeatTimeout(Duration.ofSeconds(60))
                            .build());
    @Override
    public void processReserve(Reservation reservation) {
        log.info("Process Reservation Process: "+reservation.getBook().getTitle());
        reservationProcessActivity.processReservation(reservation);
    }
}
