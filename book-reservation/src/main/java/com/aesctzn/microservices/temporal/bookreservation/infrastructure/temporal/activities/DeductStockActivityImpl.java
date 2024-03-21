package com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.activities;

import com.aesctzn.microservices.temporal.bookreservation.domain.Book;
import io.temporal.activity.Activity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DeductStockActivityImpl implements DeductStockActivity {
    @Override
    public ActivityResult deductStock(Book book) {
        ActivityResult activityResult = new ActivityResult();

        log.info("Descontando Stock para libro : "+ book.getTitle());
        activityResult.setSummary("Descontando Stock para libro : "+ book.getTitle());
        //Simulacion de excepcion para prueba de reintetos
        if (book.getId()==10) {
            log.error("Simulacion de error procesando libro");
            throw new RuntimeException();
        }

        //Simulacion de trabajo de la actividad con retardo en el envío de heartbeat
        try {
            for (int i = 0; i < 3; i++) {
                // Realiza algún trabajo
                log.info("Simulacion de carga de trabajo");
                Thread.sleep(3000);
                // Envía un latido cada segundo para indicar progreso
                Activity.getExecutionContext().heartbeat("Procesando elemento " + i);
                log.info("Enviando Heartbeat");
            }
            // Simulación de finalización del trabajo
            // Aquí iría la lógica real de la actividad
        } catch (InterruptedException e) {
            // Manejar interrupción si es necesario
        }

        return activityResult;
    }

    @Override
    public void compensateStock(Book book) {
        log.info("Ejecutando compensacion de deducción de stock de libro : "+book.getTitle());
    }
}
