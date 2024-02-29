package com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.activities;

import lombok.extern.slf4j.Slf4j;

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
