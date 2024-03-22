package com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.workflows;

import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.activities.DeductStockActivity;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.activities.LotCreationActivity;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
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
        lotCreationActivity.doLots();
        log.info("Finalizando Scheduler Workflow ");
        return new WorkflowResult();
    }
}
