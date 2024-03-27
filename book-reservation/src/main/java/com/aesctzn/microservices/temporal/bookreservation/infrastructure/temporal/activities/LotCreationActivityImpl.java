package com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.activities;

import com.aesctzn.microservices.temporal.bookreservation.domain.Book;
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
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return getResult();
    }

    private List<Reservation> getResult(){
        List<Reservation> result = new ArrayList<>();
        Reservation reservation = new Reservation();
        Book book = new Book();
        book.setTitle("El guardan del centeno");
        reservation.setBook(book);

        Reservation reservation1 = new Reservation();
        Book book1 = new Book();
        book1.setTitle("El señor de los anillos");
        reservation1.setBook(book1);

        Reservation reservation2 = new Reservation();
        Book book2 = new Book();
        book2.setTitle("It");
        reservation2.setBook(book2);

        result.add(reservation);
        result.add(reservation1);
        result.add(reservation2);
        return result;

    }
}
