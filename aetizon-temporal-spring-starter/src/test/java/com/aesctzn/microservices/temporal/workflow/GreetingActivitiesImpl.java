package com.aesctzn.microservices.temporal.workflow;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GreetingActivitiesImpl implements  GreetingActivities{

    @Override
    public String composeGreeting(String greeting, String name) {
        log.info("Composing greeting...");
        return greeting + " " + name + "!";
    }
}
