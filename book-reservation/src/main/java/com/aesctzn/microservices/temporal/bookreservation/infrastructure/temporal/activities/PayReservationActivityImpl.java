package com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.activities;

import com.aesctzn.microservices.temporal.bookreservation.domain.Book;
import com.aesctzn.microservices.temporal.bookreservation.domain.Reservation;
import io.temporal.activity.Activity;
import io.temporal.activity.ActivityExecutionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PayReservationActivityImpl implements  PayReservationActivity {
    @Override
    public ActivityResult doPay(Reservation reservation) {
        //No se marcará a complete de forma automática
        if (reservation.getBook().getId()==11) {
            log.error("Simulacion de error pagando libro");
            throw new RuntimeException();
        }

        Activity.getExecutionContext().useLocalManualCompletion();
        ActivityResult activityResult = new ActivityResult();
        log.info("Inicializando pago");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if(reservation.getBook().getId() == 15) {

            //No se puede gestionar con try catch dentro de las actividades
            try {
                log.info("Finalizar con Fail en caso código de libro");
                throw  new RuntimeException();
            } catch (Exception e){
                log.info("Error controlado por try catch");
                Activity.getExecutionContext().useLocalManualCompletion().fail(new RuntimeException("El código del libro es invalido"));
                Activity.getExecutionContext().useLocalManualCompletion().reportCancellation("Cancelado Reserva ya realizada para el código");
            }
        }else {
            log.info("Pago Realizado para la reserva del libro : " + reservation.getBook().getTitle());
            activityResult.setSummary("Pago realizado para el libro : " + reservation.getBook().getTitle());
            Activity.getExecutionContext().useLocalManualCompletion().complete(activityResult);
        }

        //imprimir información de Actividad de ejecucion
        log.info("Activity Info :" + Activity.getExecutionContext().getInfo().toString());
        log.info("Activity Token :" + Activity.getExecutionContext().getTaskToken());

        return activityResult;
    }

    @Override
    public void compensatePay(Book book) {
        log.info("Ejecutando compensación de pago de libro :"+book.getTitle());
    }
}
