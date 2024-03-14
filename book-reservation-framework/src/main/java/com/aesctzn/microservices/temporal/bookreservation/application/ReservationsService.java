package com.aesctzn.microservices.temporal.bookreservation.application;

import com.aesctzn.microservices.temporal.bookreservation.domain.Reservation;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.activities.NotificationsActivityImpl;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.workflows.*;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.activities.DeductStockActivityImpl;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.activities.PayReservationActivityImpl;
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
    private WorkflowClient workflowClient;


    @Override
    @Async
    public void doReservation(Reservation reservation) {

        MDC.put("X-Authorization","Bearer:1232323324423423423");

        WorkflowOptions workflowOptions = WorkflowOptions.newBuilder()
                .setTaskQueue(TASK_QUEUE)
                .setWorkflowId(reservation.getBook().getTitle())
                //Propagacion de Contexto
                .setContextPropagators(Collections.singletonList(new MDCContextPropagator()))
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


    public class MDCContextPropagator implements ContextPropagator {
        public String getName() {
            return this.getClass().getName();
        }

        public Object getCurrentContext() {
            Map<String, String> context = new HashMap<>();
            for (Map.Entry<String, String> entry : MDC.getCopyOfContextMap().entrySet()) {
                if (entry.getKey().startsWith("X-")) {
                    context.put(entry.getKey(), entry.getValue());
                }
            }
            return context;
        }

        public void setCurrentContext(Object context) {
            Map<String, String> contextMap = (Map<String, String>) context;
            for (Map.Entry<String, String> entry : contextMap.entrySet()) {
                MDC.put(entry.getKey(), entry.getValue());
            }
        }

        public Map<String, Payload> serializeContext(Object context) {
            Map<String, String> contextMap = (Map<String, String>) context;
            Map<String, Payload> serializedContext = new HashMap<>();
            for (Map.Entry<String, String> entry : contextMap.entrySet()) {
                serializedContext.put(entry.getKey(), DataConverter.getDefaultInstance().toPayload(entry.getValue()).get());
            }
            return serializedContext;
        }

        public Object deserializeContext(Map<String, Payload> context) {
            Map<String, String> contextMap = new HashMap<>();
            for (Map.Entry<String, Payload> entry : context.entrySet()) {
                contextMap.put(entry.getKey(), DataConverter.getDefaultInstance().fromPayload(entry.getValue(), String.class, String.class));
            }
            return contextMap;
        }
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


    @Override
    @Async
    public void doHello(String saludo) {

        MDC.put("X-Authorization","Bearer:1232323324423423423");

        WorkflowOptions workflowOptions = WorkflowOptions.newBuilder()
                .setTaskQueue("helloQueue")
                .setWorkflowId(saludo)
                //Propagacion de Contexto
                .setContextPropagators(Collections.singletonList(new MDCContextPropagator()))
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


        HelloWorkflow workflow = workflowClient.newWorkflowStub(HelloWorkflow.class,workflowOptions);
        WorkflowResult result = workflow.doHello(saludo);
        log.info(result.getSummary()); ;

    }
}
