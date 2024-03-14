package com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
@Data
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "temporal.connection")
public class TemporalConnectionProperties {

    private String host;

    private int port;

    private boolean useSsl = false;

    private boolean defaultConfiguration = true;
}
