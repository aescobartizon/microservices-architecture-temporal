package com.aesctzn.microservices.temporal.bookreservation.application;

import com.aesctzn.microservices.starter.temporal.interfaces.TemporalManagement;
import com.aesctzn.microservices.temporal.bookreservation.domain.Reservation;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.activities.*;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.workflows.*;
import io.temporal.api.common.v1.Payload;
import io.temporal.api.enums.v1.WorkflowIdReusePolicy;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.common.RetryOptions;
import io.temporal.common.context.ContextPropagator;
import io.temporal.common.converter.DataConverter;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
@Slf4j
public class ReservationsService implements Reservations {

    private static final String TASK_QUEUE = "booksReservations";

    @Autowired
    private TemporalManagement temporalManagement;

    @Autowired
    DeductStockActivity deductStockActivity;

    @Autowired
    NotificationsActivity notificationsActivity;

    @Autowired
    PayReservationActivity payReservationActivity;

    @PostConstruct
    public void initTemporalIntegration(){
        temporalManagement.getWorker(TASK_QUEUE).registerWorkflowImplementationTypes(ReservationsWorkflowTemporalSaga.class);
        temporalManagement.getWorker(TASK_QUEUE).registerActivitiesImplementations(deductStockActivity, payReservationActivity, notificationsActivity);
        temporalManagement.getWorkerFactory().start();
    }


    @Override
    @Async
    public void doReservation(Reservation reservation) {

        ReservationsWorkflow workflow = temporalManagement.getWorkflowClient().newWorkflowStub(ReservationsWorkflow.class,temporalManagement.getWorkflowOptions(TASK_QUEUE,reservation.getBook().getTitle()));
        WorkflowResult result = workflow.doReservation(reservation);
        log.info(result.getSummary()); ;

    }




    @Override
    public void sendNotification(SignalNotifications notification) {
        ReservationsWorkflow workflowById = temporalManagement.getWorkflowClient().newWorkflowStub(ReservationsWorkflow.class, notification.getReservation().getBook().getTitle());
        workflowById.sendNotification(notification);
    }

    @Override
    public Reservation getReservationInfo(String bookTitle) {
        ReservationsWorkflow workflowById = temporalManagement.getWorkflowClient().newWorkflowStub(ReservationsWorkflow.class, bookTitle);
        return workflowById.getReservationInfo();
    }
}
