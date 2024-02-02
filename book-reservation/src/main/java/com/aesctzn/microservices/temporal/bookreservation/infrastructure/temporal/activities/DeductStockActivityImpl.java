package com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.activities;

import com.aesctzn.microservices.temporal.bookreservation.domain.Book;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeductStockActivityImpl implements DeductStockActivity {
    @Override
    public ActivityResult deductStock(Book book) {
        ActivityResult activityResult = new ActivityResult();
        log.info("Descontando Stock para libro : "+ book.getTitle());
        activityResult.setSummary("Descontando Stock para libro : "+ book.getTitle());
        if (book.getId()==10) {
            throw new RuntimeException();
        }
        return activityResult;
    }
}
