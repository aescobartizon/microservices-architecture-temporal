package com.aesctzn.microservices.temporal.bookreservation.adapter.inbound;

import com.aesctzn.microservices.temporal.bookreservation.application.Reservations;
import com.aesctzn.microservices.temporal.bookreservation.domain.Book;
import com.aesctzn.microservices.temporal.bookreservation.domain.Reservation;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.historical.HistoricalService;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.historical.QueryExecutionsDto;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.namespaces.NamespaceManagement;
import com.aesctzn.microservices.temporal.bookreservation.infrastructure.temporal.namespaces.NamespaceRegisterDto;
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

    @Autowired
    private NamespaceManagement namespaceManagement;

    @Autowired
    private HistoricalService historicalService;

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


    @PostMapping("/namespace")
    public ResponseEntity<String> createNamespace(@RequestBody NamespaceRegisterDto namespaceRegisterDto) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(namespaceManagement.createNamespace(namespaceRegisterDto).toString());
    }

    @PostMapping("/describeNamespace")
    public ResponseEntity<String> describeNamespace(@RequestBody NamespaceRegisterDto namespaceRegisterDto) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(namespaceManagement.describeNamespace(namespaceRegisterDto.getName()).getNamespaceInfo().toString());
    }
    @PostMapping("/updateNamespace")
    public ResponseEntity<String> updateNamespace(@RequestBody NamespaceRegisterDto namespaceRegisterDto) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(namespaceManagement.updateNamespace(namespaceRegisterDto).toString());
    }

    @PostMapping("/deprecateNamespace")
    public ResponseEntity<String> deprecateNamespace(@RequestBody NamespaceRegisterDto namespaceRegisterDto) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(namespaceManagement.describeNamespace(namespaceRegisterDto.getName()).toString());
    }

    @GetMapping("/listNamespaces")
    public ResponseEntity<String> listNamespaces() {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(namespaceManagement.getListNamespaces().toString());
    }

    @PostMapping("/executionsList")
    public ResponseEntity<String> executionList(@RequestBody QueryExecutionsDto queryExecutionsDto) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(historicalService.getExecutionsResponse(queryExecutionsDto).toString());
    }

    @PostMapping("/executionsHistoricalList")
    public ResponseEntity<String> executionHistoricalList(@RequestBody QueryExecutionsDto queryExecutionsDto) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(historicalService.getHistoryExecutionsResponse(queryExecutionsDto).toString());
    }

    @GetMapping("/hello")
    public ResponseEntity<String> getHello(@RequestParam("hello") String saludo){
        reservationService.doHello(saludo);
        return ResponseEntity.status(HttpStatus.OK).body("Saludo enviado");
    }

    private Book findBookById(Long bookId) {
        return new Book();
    }
}