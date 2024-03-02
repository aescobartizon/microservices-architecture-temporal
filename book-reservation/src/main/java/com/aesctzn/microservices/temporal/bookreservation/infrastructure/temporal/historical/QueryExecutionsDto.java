package com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.historical;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QueryExecutionsDto {

    private String name;
    private String query;
    private String workflowId;
}
