package com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.workflows;

import com.aesctzn.microservices.temporal.bookreservation.domain.Reservation;
import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface ReservationsWorkflow {

    @WorkflowMethod
    WorkflowResult doReservation(Reservation reservation);

    @SignalMethod
    void sendNotification(SignalNotifications signalNotifications);

    @QueryMethod
    WorkflowResult getCurrentWorkflowResult();

    @QueryMethod
    Reservation getReservationInfo();
}
