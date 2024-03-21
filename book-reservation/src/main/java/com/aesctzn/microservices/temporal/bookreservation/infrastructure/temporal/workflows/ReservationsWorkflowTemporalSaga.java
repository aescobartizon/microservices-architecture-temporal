package com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.workflows;

import com.aesctzn.microservices.temporal.bookreservation.domain.Reservation;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.activities.ActivityResult;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.activities.DeductStockActivity;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.activities.NotificationsActivity;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.activities.PayReservationActivity;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class ReservationsWorkflowTemporalSaga implements ReservationsWorkflow {

    private final DeductStockActivity deductStockActivity =
            Workflow.newActivityStub(
                    DeductStockActivity.class,
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

    private final PayReservationActivity payReservationActivity=
            Workflow.newActivityStub(
                    PayReservationActivity.class,
                    ActivityOptions.newBuilder() .setStartToCloseTimeout(Duration.ofSeconds(2))
                            .setStartToCloseTimeout(Duration.ofSeconds(10))
                            .setRetryOptions(RetryOptions.newBuilder()
                                    .setInitialInterval(Duration.ofSeconds(5)) // Intervalo inicial entre reintentos
                                    .setMaximumAttempts(3) // Número máximo de reintentos
                                    .setDoNotRetry(String.valueOf(IllegalArgumentException.class)) // No volver a intentar para excepciones específicas
                                    .build())
                            .build());

    private final NotificationsActivity notificationsActivity=
            Workflow.newActivityStub(
                    NotificationsActivity.class,
                    ActivityOptions.newBuilder()
                            .setStartToCloseTimeout(Duration.ofSeconds(10))
                            .setRetryOptions(RetryOptions.newBuilder()
                                    .setInitialInterval(Duration.ofSeconds(5)) // Intervalo inicial entre reintentos
                                    .setMaximumAttempts(3) // Número máximo de reintentos
                                    .setDoNotRetry(String.valueOf(IllegalArgumentException.class)) // No volver a intentar para excepciones específicas
                                    .build())
                            .build());


    //Guardará resultados parciales del Workflow que podremos consultar
    private WorkflowResult result = new WorkflowResult();

    // Se guardaŕa el parámetro de entrada de la ejecución del Workflow para que se pueda consultar si es necesario
    private Reservation reservationInfo;

    private SignalNotifications signalNotifications = new SignalNotifications();

    private String titulo;
    private ActivityResult resultDeductStock;

    private String status= "";


    @Override
    public WorkflowResult doReservation(Reservation reservation)  {

        Saga saga = new Saga(new Saga.Options.Builder().setParallelCompensation(false).build());
        try {
            saga.addCompensation(deductStockActivity::compensateStock, reservation.getBook());
            deductStockActivity.deductStock(reservation.getBook());
            saga.addCompensation(payReservationActivity::compensatePay, reservation.getBook());
            payReservationActivity.doPay(reservation);

            List<String> notifications = Arrays.asList("Antonio","Jose","Pepe","Luis","Ricardo","Andres","Gema","Pilar","Clara");
            List<Promise<String>> promiseList = new ArrayList<>();
            notifications.stream().forEach(p -> promiseList.add(Async.function(notificationsActivity::sendNotifications,"Hola Reserva Completada "+p)));

            //Ejecución de todas las tareas concurrentes y esperar a que teminen
            Promise.allOf(promiseList).get();


        }catch (Exception e){
            log.info("Tratamiento de errores ");
            saga.compensate();
            List<String> notifications = Arrays.asList("Antonio","Jose","Pepe","Luis","Ricardo","Andres","Gema","Pilar","Clara");
            List<Promise<String>> promiseList = new ArrayList<>();
            notifications.stream().forEach(p -> promiseList.add(Async.function(notificationsActivity::sendNotifications,"Reserva no procesada "+p)));

            //Ejecución de todas las tareas concurrentes y esperar a que teminen
            Promise.allOf(promiseList).get();
        }

        return result;
    }

    @Override
    public void sendNotification(SignalNotifications signalNotifications) {
        log.info("Notificación Recibida");
        this.signalNotifications = signalNotifications;
    }

    @Override
    public WorkflowResult getCurrentWorkflowResult() {
        return result;
    }

    @Override
    public Reservation getReservationInfo() {
        return reservationInfo;
    }
}
