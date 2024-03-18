package com.aesctzn.microservices.starter.temporal.services.historical;

import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.workflowservice.v1.GetWorkflowExecutionHistoryRequest;
import io.temporal.api.workflowservice.v1.GetWorkflowExecutionHistoryResponse;
import io.temporal.api.workflowservice.v1.ListWorkflowExecutionsRequest;
import io.temporal.api.workflowservice.v1.ListWorkflowExecutionsResponse;
import io.temporal.serviceclient.WorkflowServiceStubs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HistoricalService {

    @Autowired
    private WorkflowServiceStubs serviceStubs;


    public ListWorkflowExecutionsResponse getExecutionsResponse(QueryExecutionsDto queryExecutionsDto) {

        //  .setQuery("WorkflowType='TestWorkflow'")
        ListWorkflowExecutionsRequest listWorkflowExecutionRequest =
                ListWorkflowExecutionsRequest.newBuilder()
                        .setNamespace(queryExecutionsDto.getName())
                        //.setQuery("WorkflowId='El club de los poetas muertos'")
                        .build();
        return serviceStubs.blockingStub().listWorkflowExecutions(listWorkflowExecutionRequest);
    }

    public GetWorkflowExecutionHistoryResponse getHistoryExecutionsResponse(QueryExecutionsDto queryExecutionsDto) {

        GetWorkflowExecutionHistoryRequest request =  GetWorkflowExecutionHistoryRequest.newBuilder()
                .setNamespace(queryExecutionsDto.getName())
                .setExecution(WorkflowExecution.newBuilder()
                        .setWorkflowId(queryExecutionsDto.getWorkflowId())
                        .build())
                .build();

        return serviceStubs.blockingStub().getWorkflowExecutionHistory(request);
    }
}
