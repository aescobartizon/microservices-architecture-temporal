package com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.workflows;

import com.aesctzn.microservices.temporal.bookreservation.domain.Reservation;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.activities.*;
import io.temporal.activity.LocalActivityOptions;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
@Slf4j
public class ReservationsWorkflowTemporal implements ReservationsWorkflow {

    private final DeductStockActivity deductStockActivity =
            Workflow.newLocalActivityStub(
                    DeductStockActivity.class,
                    LocalActivityOptions.newBuilder()
                            .setStartToCloseTimeout(Duration.ofSeconds(2))
                            .build());

    private final PayReservationActivity payReservationActivity=
            Workflow.newLocalActivityStub(
                    PayReservationActivity.class,
                    LocalActivityOptions.newBuilder()
                            .setStartToCloseTimeout(Duration.ofSeconds(2))
                            .build());


    private WorkflowResult result = new WorkflowResult();

    private boolean running = false;

    private String titulo;
    private ActivityResult resultDeductStock;


    @Override
    public WorkflowResult doReservation(Reservation reservation) {

        titulo = reservation.getBook().getTitle();

        log.info("Ejecutando WF Reserva de libro "+ reservation.getBook().getTitle());

        ActivityResult resultDeductStock = deductStockActivity.deductStock(reservation.getBook());

        result.setSummary(result.getSummary()+resultDeductStock.getSummary());

        Workflow.await(()->running);

        ActivityResult payReservationResult = payReservationActivity.doPay(reservation);

        result.setSummary(result.getSummary()+" Reserva confirmada "+payReservationResult.getSummary());

        return result;
    }
}
