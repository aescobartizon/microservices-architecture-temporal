package com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.workflows.childs;

import com.aesctzn.microservices.temporal.bookreservation.domain.Reservation;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface ReservationProcessWorkflow {
    @WorkflowMethod
    void processReserve(Reservation reservation);
}
