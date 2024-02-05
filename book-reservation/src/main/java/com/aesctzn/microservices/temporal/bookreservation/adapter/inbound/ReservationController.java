package com.aesctzn.microservices.temporal.bookreservation.adapter.inbound;

import com.aesctzn.microservices.temporal.bookreservation.application.Reservations;
import com.aesctzn.microservices.temporal.bookreservation.domain.Book;
import com.aesctzn.microservices.temporal.bookreservation.domain.Reservation;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.workflows.SignalNotifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class ReservationController {

    private List<Book> books = new ArrayList<>();

    @Autowired
    private Reservations reservationService;

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(books);
    }

    @PostMapping("/reserve")
    public ResponseEntity<String> reserveBook(@RequestBody Reservation request) {
        reservationService.doReservation(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Reserva realizada para libro +"+request.getBook().getTitle());
    }

    @PostMapping("/notification")
    public ResponseEntity<String> reserveBook(@RequestBody SignalNotifications notification) {
        reservationService.sendNotification(notification);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Notificación enviada para usuario y libro +"+notification.getReservation().getBook().getTitle());
    }

    @GetMapping("/info")
    public ResponseEntity<Reservation> getReservationInfo(@RequestParam("bookTitle") String bookTitle){
        return ResponseEntity.status(HttpStatus.OK).body(reservationService.getReservationInfo(bookTitle));
    }

    private Book findBookById(Long bookId) {
        return new Book();
    }
}