package com.aesctzn.microservices.temporal.bookreservation.application;

import com.aesctzn.microservices.starter.temporal.interfaces.TemporalManagement;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.activities.DeductStockActivity;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.activities.LotCreationActivity;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.activities.NotificationsActivity;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.activities.PayReservationActivity;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.workflows.ReservationsWorkflowTemporalSaga;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.workflows.SchedulerReservationsBillingWorkflowImpl;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.workflows.childs.ReservationProcessActivity;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.workflows.childs.ReservationProcessWorkflow;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.workflows.childs.ReservationProcessWorkflowImpl;
import io.temporal.client.schedules.ScheduleClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkFlowsRegister {

    private static final String TASK_QUEUE = "booksReservations";

    @Autowired
    private TemporalManagement temporalManagement;

    @Autowired
    private WorkflowServiceStubs serviceStubs;

    @Autowired
    private LotCreationActivity lotCreationActivity;

    @Autowired
    DeductStockActivity deductStockActivity;

    @Autowired
    NotificationsActivity notificationsActivity;

    @Autowired
    PayReservationActivity payReservationActivity;

    @Autowired
    ReservationProcessActivity reservationProcessActivity;

    @Bean
    public ScheduleClient initTemporalIntegration(){
        temporalManagement.getWorker(TASK_QUEUE).registerWorkflowImplementationTypes(ReservationsWorkflowTemporalSaga.class);
        temporalManagement.getWorker(TASK_QUEUE).registerActivitiesImplementations(deductStockActivity, payReservationActivity, notificationsActivity);

        temporalManagement.getWorker(TASK_QUEUE).registerWorkflowImplementationTypes(SchedulerReservationsBillingWorkflowImpl.class);
        temporalManagement.getWorker(TASK_QUEUE).registerActivitiesImplementations(lotCreationActivity);

        temporalManagement.getWorker(TASK_QUEUE).registerWorkflowImplementationTypes(ReservationProcessWorkflowImpl.class);
        temporalManagement.getWorker(TASK_QUEUE).registerActivitiesImplementations(reservationProcessActivity);

        temporalManagement.getWorkerFactory().start();

       return  ScheduleClient.newInstance(serviceStubs);


    }


}
