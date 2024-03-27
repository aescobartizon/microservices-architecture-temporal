package com.aesctzn.microservices.temporal.bookreservation.application;

import com.aesctzn.microservices.starter.temporal.interfaces.TemporalManagement;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.workflows.SchedulerReservationsBillingWorkflow;
import io.temporal.api.enums.v1.ScheduleOverlapPolicy;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.schedules.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;

import static java.time.LocalTime.now;
@Slf4j
@Service
public class ReservationSchedulerService {

    @Autowired
    private TemporalManagement temporalManagement;

    static final String SCHEDULE_ID = "BookReservationsProcessSchedule";

    static final String WORKFLOW_ID = "BookReservationsScheduleWorkflow";

    private ScheduleHandle handle;
    @Autowired
    private ScheduleClient scheduleClient;

    private static final String TASK_QUEUE = "booksReservations";

    @PostConstruct
    public void initTemporalIntegration(){

        createSchedulerWorkflow();
    }

    private void createSchedulerWorkflow(){


        WorkflowOptions workflowOptions = WorkflowOptions.newBuilder()
                .setWorkflowId(WORKFLOW_ID)
                .setTaskQueue(TASK_QUEUE)
                .setWorkflowRunTimeout(Duration.ofMinutes(1))
                .build();

        ScheduleActionStartWorkflow action =
                ScheduleActionStartWorkflow.newBuilder()
                        .setWorkflowType(SchedulerReservationsBillingWorkflow.class)
                        .setArguments(now().toString())
                        .setOptions(workflowOptions)
                        .build();

        Schedule schedule = Schedule.newBuilder().setAction(action).setSpec(ScheduleSpec.newBuilder().build()).build();

        try {
            handle = scheduleClient.createSchedule(SCHEDULE_ID, schedule, ScheduleOptions.newBuilder().build());

            updateScheduleTigger();

        }catch (Exception e){
            log.info("Scheduler Registrado en el sistema");
            handle = scheduleClient.getHandle(SCHEDULE_ID);
            log.info(handle.describe().toString());
            updateScheduleTigger();
        }
        handle.unpause();

    }

    private void updateScheduleTigger() {

        handle.trigger(ScheduleOverlapPolicy.SCHEDULE_OVERLAP_POLICY_TERMINATE_OTHER);
        // Update the schedule with a spec, so it will run periodically
        handle.update(
                (ScheduleUpdateInput input) -> {
                    Schedule.Builder builder = Schedule.newBuilder(input.getDescription().getSchedule());

                    builder.setSpec(
                            ScheduleSpec.newBuilder()
                                    // Run the schedule at 5pm on Friday
                                    .setCalendars(
                                            Collections.singletonList(
                                                    ScheduleCalendarSpec.newBuilder()
                                                            .setHour(Collections.singletonList(new ScheduleRange(17)))
                                                            .setDayOfWeek(Collections.singletonList(new ScheduleRange(5)))
                                                            .build()))
                                    // Run the schedule every 20s
                                    .setIntervals(
                                            Collections.singletonList(new ScheduleIntervalSpec(Duration.ofSeconds(30))))
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
    }
}
