package com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.activities;

import com.aesctzn.microservices.temporal.bookreservation.domain.Reservation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class LotCreationActivityImpl implements  LotCreationActivity {
    @Override
    public List<Reservation> doLots() {
        log.info("Realizando particiones");
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>();
    }
}
