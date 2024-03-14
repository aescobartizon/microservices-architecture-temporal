package com.aesctzn.microservices.temporal.bookreservation.application;

import com.aesctzn.microservices.temporal.bookreservation.domain.Book;
import org.springframework.http.ResponseEntity;

public interface ManagementBookStock {

    ResponseEntity<String> deductStock(Book book);
}
