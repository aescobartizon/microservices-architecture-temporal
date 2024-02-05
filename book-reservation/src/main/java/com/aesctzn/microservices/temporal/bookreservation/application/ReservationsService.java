package com.aesctzn.microservices.temporal.bookreservation.application;

import com.aesctzn.microservices.temporal.bookreservation.domain.Reservation;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.workflows.ReservationsWorkflow;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.workflows.ReservationsWorkflowTemporal;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.workflows.SignalNotifications;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.workflows.WorkflowResult;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.activities.DeductStockActivityImpl;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.activities.PayReservationActivityImpl;
import io.temporal.api.enums.v1.WorkflowIdReusePolicy;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.common.RetryOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
public class ReservationsService implements Reservations {

    private static final String TASK_QUEUE = "booksReservations";
    @Autowired
    private WorkerFactory workerFactory;
    @Autowired
    private WorkflowClient workflowClient;
    private WorkflowOptions build;

    @PostConstruct
    public void initTemporalIntegration(){
        Worker reservationsWorkflowWorker = workerFactory.newWorker(TASK_QUEUE);
        reservationsWorkflowWorker.registerWorkflowImplementationTypes(ReservationsWorkflowTemporal.class);
        reservationsWorkflowWorker.registerActivitiesImplementations(new DeductStockActivityImpl(), new PayReservationActivityImpl());
        workerFactory.start();
    }


    @Override
    @Async
    public void doReservation(Reservation reservation) {

        WorkflowOptions workflowOptions = WorkflowOptions.newBuilder()
                .setTaskQueue(TASK_QUEUE)
                .setWorkflowId(reservation.getBook().getTitle())
                //WORKFLOW_ID_REUSE_POLICY//
                .setWorkflowIdReusePolicy(WorkflowIdReusePolicy.WORKFLOW_ID_REUSE_POLICY_ALLOW_DUPLICATE)
                //.setWorkflowIdReusePolicy(WorkflowIdReusePolicy.WORKFLOW_ID_REUSE_POLICY_REJECT_DUPLICATE)
                //.setWorkflowIdReusePolicy(WorkflowIdReusePolicy.WORKFLOW_ID_REUSE_POLICY_ALLOW_DUPLICATE_FAILED_ONLY)
                //.setWorkflowIdReusePolicy(WorkflowIdReusePolicy.WORKFLOW_ID_REUSE_POLICY_TERMINATE_IF_RUNNING)

                //TIME OUT//
                .setWorkflowRunTimeout(Duration.ofMinutes(15))
                //.setWorkflowRunTimeout(Duration.ofSeconds(5))
                //POLITICA DE REINTENTOS//
                .setRetryOptions(RetryOptions.newBuilder()
                        .setBackoffCoefficient(2)
                        .setInitialInterval(Duration.ofSeconds(2))
                        .setMaximumAttempts(3)
                        .setMaximumInterval(Duration.ofSeconds(10))
                               .build()
                )
                .build();


        ReservationsWorkflow workflow = workflowClient.newWorkflowStub(ReservationsWorkflow.class,workflowOptions);
        WorkflowResult result = workflow.doReservation(reservation);
        log.info(result.getSummary()); ;

    }

    @Override
    public void sendNotification(SignalNotifications notification) {
        ReservationsWorkflow workflowById = workflowClient.newWorkflowStub(ReservationsWorkflow.class, notification.getReservation().getBook().getTitle());
        workflowById.sendNotification(notification);
    }

    @Override
    public Reservation getReservationInfo(String bookTitle) {
        ReservationsWorkflow workflowById = workflowClient.newWorkflowStub(ReservationsWorkflow.class, bookTitle);
        return workflowById.getReservationInfo();
    }
}
