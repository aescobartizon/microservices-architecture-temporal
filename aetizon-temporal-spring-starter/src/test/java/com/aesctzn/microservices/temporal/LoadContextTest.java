package com.aesctzn.microservices.temporal;

import com.aesctzn.microservices.starter.temporal.interfaces.TemporalManagement;
import com.aesctzn.microservices.temporal.workflow.GreetingActivitiesImpl;
import com.aesctzn.microservices.temporal.workflow.GreetingWorkflow;
import com.aesctzn.microservices.temporal.workflow.GreetingWorkflowImpl;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.testing.TestWorkflowExtension;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@SpringBootConfiguration
@ComponentScan("com.aesctzn")
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yaml")
public class LoadContextTest {

    @Autowired
    TemporalManagement temporalManagement;

    @RegisterExtension
    public static final TestWorkflowExtension testWorkflowExtension =
            TestWorkflowExtension.newBuilder()
                    .setWorkflowTypes(GreetingWorkflowImpl.class)
                    .setDoNotStart(true)
                    .build();

    @Test
    public void testActivityImpl(TestWorkflowEnvironment testEnv, Worker worker, GreetingWorkflow workflow) {
        worker.registerActivitiesImplementations(new GreetingActivitiesImpl());
        testEnv.start();

        String greeting = workflow.getGreeting("World");
        assertEquals("Hello World!", greeting);
    }

    @Test
    public void loadContext(){
        assertNotNull(temporalManagement);
    }
}
