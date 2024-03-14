package com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface NotificationsActivity {
    @ActivityMethod
    String sendNotifications(String notification);


}
