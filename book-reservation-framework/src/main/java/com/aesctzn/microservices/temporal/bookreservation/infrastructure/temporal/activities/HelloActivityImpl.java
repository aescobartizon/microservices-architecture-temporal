package com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.activities;

import io.temporal.spring.boot.ActivityImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ActivityImpl(taskQueues = "helloQueue")
public class HelloActivityImpl implements  HelloActivity {

    @Override
    public ActivityResult sendHello(String saludo) {
        log.info("Saludo enviada: "+ saludo);
        return new ActivityResult();
    }
}
