package com.aesctzn.microservices.temporal.bookreservation.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Book {
    public Book (){

    }
    private Long id;
    private String title;
    private String author;
}