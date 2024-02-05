package com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.workflows;

import com.aesctzn.microservices.temporal.bookreservation.domain.Reservation;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.activities.*;
import io.temporal.activity.LocalActivityOptions;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
@Slf4j
public class ReservationsWorkflowTemporal implements ReservationsWorkflow {

    private final DeductStockActivity deductStockActivity =
            Workflow.newLocalActivityStub(
                    DeductStockActivity.class,
                    LocalActivityOptions.newBuilder()
                            .setStartToCloseTimeout(Duration.ofSeconds(2))
                            .build());

    private final PayReservationActivity payReservationActivity=
            Workflow.newLocalActivityStub(
                    PayReservationActivity.class,
                    LocalActivityOptions.newBuilder()
                            .setStartToCloseTimeout(Duration.ofSeconds(2))
                            .build());


    //Guardará resultados parciales del Workflow que podremos consultar
    private WorkflowResult result = new WorkflowResult();

    // Se guardaŕa el parámetro de entrada de la ejecución del Workflow para que se pueda consultar si es necesario
    private Reservation reservationInfo;

    private SignalNotifications signalNotifications = new SignalNotifications();

    private String titulo;
    private ActivityResult resultDeductStock;


    @Override
    public WorkflowResult doReservation(Reservation reservation) {

        this.reservationInfo = reservation;

        titulo = reservation.getBook().getTitle();

        log.info("Ejecutando WF Reserva de libro "+ reservation.getBook().getTitle());

        ActivityResult resultDeductStock = deductStockActivity.deductStock(reservation.getBook());

        result.setSummary(result.getSummary()+resultDeductStock.getSummary());

        ActivityResult payReservationResult = payReservationActivity.doPay(reservation);

        result.setSummary(result.getSummary()+" Reserva confirmada "+payReservationResult.getSummary());

        //Parcial Status para consulta
        reservation.setStatus("PAY Complete. Waiting for Notification");

        //Esperamos a señal de servicio externo envíe una notificación
        Workflow.await(()->signalNotifications.isSendNotification());

        //En funcion de la notificación podemos
        if(signalNotifications.getSeviceName().equals("EMAIL")){
            log.info("Envío completado con notificación con email");
        }else{
            log.info("Envío completado con metodo alternativo");
        }

        //Actualizacion del estado de la reserva para posterior consulta
        reservation.setStatus("PAY Complete. Notification Complete");

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
