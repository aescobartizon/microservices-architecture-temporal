package com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.activities;

import com.aesctzn.microservices.temporal.bookreservation.domain.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationsActivityImpl implements  NotificationsActivity {
    @Override
    public String sendNotifications(String notification)  {
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("Notificacion enviada: "+ notification);
        return "Notificación enviada "+ notification;
    }



}
