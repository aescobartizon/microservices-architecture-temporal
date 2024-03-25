package com.aesctzn.microservices.starter.temporal.interfaces;

import com.aesctzn.microservices.starter.temporal.services.historical.HistoricalService;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

public interface TemporalManagement {

    WorkflowOptions getWorkflowOptions(String taskQueue, String workflowId);

    Worker getWorker(String taskQueue);

    WorkflowClient getWorkflowClient();

    WorkerFactory getWorkerFactory();

    HistoricalService getHistoricalService();
}
