package com.aesctzn.microservices.starter.temporal.services;

import com.aesctzn.microservices.starter.temporal.interfaces.TemporalManagement;
import com.aesctzn.microservices.starter.temporal.services.historical.HistoricalService;
import com.aesctzn.microservices.starter.temporal.services.namespaces.NamespaceManagement;
import io.temporal.api.common.v1.Payload;
import io.temporal.api.enums.v1.WorkflowIdReusePolicy;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.common.RetryOptions;
import io.temporal.common.context.ContextPropagator;
import io.temporal.common.converter.DataConverter;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import lombok.Getter;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class TemporalManagementImpl implements TemporalManagement {

    @Autowired
    private NamespaceManagement namespaceManagement;

    @Autowired
    @Getter
    HistoricalService historicalService;

    @Autowired
    private WorkflowServiceStubs serviceStubs;

    @Autowired
    private WorkerFactory workerFactory;
    @Autowired
    private WorkflowClient workflowClient;

    private Map<String,Worker> workers = new HashMap<>();

    @Override
    public WorkflowOptions getWorkflowOptions(String taskQueue, String workflowId) {
        return  WorkflowOptions.newBuilder()
                .setTaskQueue(taskQueue)
                .setWorkflowId(workflowId)
                //WORKFLOW_ID_REUSE_POLICY//
                .setWorkflowIdReusePolicy(WorkflowIdReusePolicy.WORKFLOW_ID_REUSE_POLICY_ALLOW_DUPLICATE)
                //.setWorkflowIdReusePolicy(WorkflowIdReusePolicy.WORKFLOW_ID_REUSE_POLICY_REJECT_DUPLICATE)
                //.setWorkflowIdReusePolicy(WorkflowIdReusePolicy.WORKFLOW_ID_REUSE_POLICY_ALLOW_DUPLICATE_FAILED_ONLY)
                //.setWorkflowIdReusePolicy(WorkflowIdReusePolicy.WORKFLOW_ID_REUSE_POLICY_TERMINATE_IF_RUNNING)

                //TIME OUT//
                .setWorkflowRunTimeout(Duration.ofMinutes(15))
                //.setWorkflowRunTimeout(Duration.ofSeconds(5))
                //POLITICA DE REINTENTOS//
                .setRetryOptions(RetryOptions.newBuilder()
                        .setBackoffCoefficient(2)
                        .setInitialInterval(Duration.ofSeconds(2))
                        .setMaximumAttempts(3)
                        .setMaximumInterval(Duration.ofSeconds(10))
                        .build()
                )
                .build();
    }

    @Override
    public Worker getWorker(String taskQueue){
        if (!workers.containsKey(taskQueue)){
            workers.put(taskQueue,workerFactory.newWorker(taskQueue));
        }
        return workers.get(taskQueue);
    }

    @Override
    public WorkflowClient getWorkflowClient(){
        return workflowClient;
    }

    @Override
    public WorkerFactory getWorkerFactory(){
        return workerFactory;
    }

}
