package com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.workflows;

import com.aesctzn.microservices.temporal.bookreservation.domain.Reservation;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.activities.LotCreationActivity;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.workflows.childs.ReservationProcessWorkflow;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Async;
import io.temporal.workflow.ChildWorkflowOptions;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SchedulerReservationsBillingWorkflowImpl implements  SchedulerReservationsBillingWorkflow {

    private final LotCreationActivity lotCreationActivity =
            Workflow.newActivityStub(
                    LotCreationActivity.class,
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
    public WorkflowResult doBilling(String initDate) {
        log.info("Inicializando Scheduler Workflow ");
        List<Reservation> reservations = lotCreationActivity.doLots();
        List<Promise<Void>> results = new ArrayList<>(reservations.size());

        for (Reservation reserva : reservations) {
            // Uses human friendly child id.
            String childId = Workflow.getInfo().getWorkflowId() + "/" + reserva.getBook().getTitle();
            ReservationProcessWorkflow processor =
                    Workflow.newChildWorkflowStub(
                            ReservationProcessWorkflow.class,
                            ChildWorkflowOptions.newBuilder().setWorkflowId(childId).build());
            Promise<Void> result = Async.procedure(processor::processReserve, reserva);
            results.add(result);
        }

        Promise.allOf(results).get();

        log.info("Finalizando Scheduler Workflow ");
        return new WorkflowResult();
    }
}
