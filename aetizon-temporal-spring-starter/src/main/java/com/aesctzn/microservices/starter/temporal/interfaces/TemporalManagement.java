package com.aesctzn.microservices.starter.temporal.interfaces;

import io.temporal.client.WorkflowOptions;

public interface TemporalManagement {

    WorkflowOptions getWorkflowOptions(String taskQueue, String workflowId);
}
