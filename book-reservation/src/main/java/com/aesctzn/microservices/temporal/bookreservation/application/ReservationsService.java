package com.aesctzn.microservices.temporal.bookreservation.application;

import com.aesctzn.microservices.starter.temporal.interfaces.TemporalManagement;
import com.aesctzn.microservices.temporal.bookreservation.domain.Reservation;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.activities.*;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.workflows.*;
import io.temporal.api.common.v1.Payload;
import io.temporal.api.enums.v1.ScheduleOverlapPolicy;
import io.temporal.api.enums.v1.WorkflowIdReusePolicy;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.schedules.*;
import io.temporal.common.RetryOptions;
import io.temporal.common.context.ContextPropagator;
import io.temporal.common.converter.DataConverter;
import io.temporal.serviceclient.WorkflowServiceStubs;
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

import static java.time.LocalTime.now;

@Service
@Slf4j
public class ReservationsService implements Reservations {

    private static final String TASK_QUEUE = "booksReservations";

    @Autowired
    private TemporalManagement temporalManagement;

    @Autowired
    private WorkflowServiceStubs serviceStubs;

    @Autowired
    DeductStockActivity deductStockActivity;

    @Autowired
    NotificationsActivity notificationsActivity;

    @Autowired
    PayReservationActivity payReservationActivity;

    @Autowired
    private LotCreationActivity lotCreationActivity;

    static final String SCHEDULE_ID = "HelloSchedule";

    static final String WORKFLOW_ID = "HelloScheduleWorkflow";

    private ScheduleHandle handle;

    @PostConstruct
    public void initTemporalIntegration(){
        temporalManagement.getWorker(TASK_QUEUE).registerWorkflowImplementationTypes(ReservationsWorkflowTemporalSaga.class);
        temporalManagement.getWorker(TASK_QUEUE).registerActivitiesImplementations(deductStockActivity, payReservationActivity, notificationsActivity);
        temporalManagement.getWorker(TASK_QUEUE).registerWorkflowImplementationTypes(SchedulerReservationsBillingWorkflowImpl.class);
        temporalManagement.getWorker(TASK_QUEUE).registerActivitiesImplementations(lotCreationActivity);
        temporalManagement.getWorkerFactory().start();

        createSchedulerWorkflow();
    }

    private void createSchedulerWorkflow(){

        ScheduleClient scheduleClient = ScheduleClient.newInstance(serviceStubs);

        WorkflowOptions workflowOptions = WorkflowOptions.newBuilder().setWorkflowId(WORKFLOW_ID).setTaskQueue(TASK_QUEUE).build();
        ScheduleActionStartWorkflow action =
                ScheduleActionStartWorkflow.newBuilder()
                        .setWorkflowType(SchedulerReservationsBillingWorkflow.class)
                        .setArguments(now().toString())
                        .setOptions(workflowOptions)
                        .build();

        Schedule schedule = Schedule.newBuilder().setAction(action).setSpec(ScheduleSpec.newBuilder().build()).build();

        // Create a schedule on the server
        handle = scheduleClient.createSchedule(SCHEDULE_ID, schedule, ScheduleOptions.newBuilder().build());

        // Manually trigger the schedule once
        handle.trigger(ScheduleOverlapPolicy.SCHEDULE_OVERLAP_POLICY_ALLOW_ALL);

        // Update the schedule with a spec, so it will run periodically
        handle.update(
                (ScheduleUpdateInput input) -> {
                    Schedule.Builder builder = Schedule.newBuilder(input.getDescription().getSchedule());

                    builder.setSpec(
                            ScheduleSpec.newBuilder()
                                    // Run the schedule at 5pm on Friday
                                    //.setCalendars(
                                    //        Collections.singletonList(
                                    //                ScheduleCalendarSpec.newBuilder()
                                    //                        .setHour(Collections.singletonList(new ScheduleRange(17)))
                                    //                        .setDayOfWeek(Collections.singletonList(new ScheduleRange(5)))
                                    //                        .build()))
                                    // Run the schedule every 5s
                                    .setIntervals(
                                            Collections.singletonList(new ScheduleIntervalSpec(Duration.ofSeconds(20))))
                                    .build());
                    // Make the schedule paused to demonstrate how to unpause a schedule
                    builder.setState(
                            ScheduleState.newBuilder()
                                    .setPaused(true)
                                    .setLimitedAction(true)
                                    .setRemainingActions(10)
                                    .build());
                    return new ScheduleUpdate(builder.build());
                });

        // Unpause schedule
        handle.unpause();
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
