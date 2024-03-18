package com.aesctzn.microservices.temporal.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
public class GreetingWorkflowImpl implements  GreetingWorkflow{
    /**
     * Define the GreetingActivities stub. Activity stubs are proxies for activity invocations that
     * are executed outside of the workflow thread on the activity worker, that can be on a
     * different host. Temporal is going to dispatch the activity results back to the workflow and
     * unblock the stub as soon as activity is completed on the activity worker.
     *
     * <p>In the {@link ActivityOptions} definition the "setStartToCloseTimeout" option sets the
     * overall timeout that our workflow is willing to wait for activity to complete. For this
     * example it is set to 2 seconds.
     */
    private final GreetingActivities activities =
            Workflow.newActivityStub(
                    GreetingActivities.class,
                    ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(2)).build());

    @Override
    public String getGreeting(String name) {
        // This is a blocking call that returns only after the activity has completed.
        log.info("Ejecutando Workflow");
        return activities.composeGreeting("Hello", name);
    }
}