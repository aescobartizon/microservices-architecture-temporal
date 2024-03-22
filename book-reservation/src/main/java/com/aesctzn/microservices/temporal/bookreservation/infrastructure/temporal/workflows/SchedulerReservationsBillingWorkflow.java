package com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.workflows;

import com.aesctzn.microservices.temporal.bookreservation.domain.Reservation;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.util.Date;

@WorkflowInterface
public interface SchedulerReservationsBillingWorkflow {

    @WorkflowMethod
    WorkflowResult doBilling(String initDate);

}
