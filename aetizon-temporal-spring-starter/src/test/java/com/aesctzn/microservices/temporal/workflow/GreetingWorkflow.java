package com.aesctzn.microservices.temporal.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface GreetingWorkflow {

    /**
     * This is the method that is executed when the Workflow Execution is started. The Workflow
     * Execution completes when this method finishes execution.
     */
    @WorkflowMethod
    String getGreeting(String name);
}