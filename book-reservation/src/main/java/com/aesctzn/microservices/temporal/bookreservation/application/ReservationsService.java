package com.aesctzn.microservices.temporal.bookreservation.application;

import com.aesctzn.microservices.starter.temporal.interfaces.TemporalManagement;
import com.aesctzn.microservices.temporal.bookreservation.domain.Reservation;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.activities.*;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.workflows.*;
import io.temporal.api.enums.v1.ScheduleOverlapPolicy;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.schedules.*;
import io.temporal.serviceclient.WorkflowServiceStubs;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

import static java.time.LocalTime.now;

@Service
@Slf4j
public class ReservationsService implements Reservations {

    @Autowired
    private TemporalManagement temporalManagement;

    private static final String TASK_QUEUE = "booksReservations";

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
