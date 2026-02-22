package tn.esprit.esprit_market.modules.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.event.dto.TicketRequest;
import tn.esprit.esprit_market.modules.event.dto.TicketResponse;
import tn.esprit.esprit_market.modules.event.service.TicketService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    // POST /api/events/{eventId}/tickets
    @PostMapping("/api/events/{eventId}/tickets")
    public ResponseEntity<TicketResponse> createTicket(@PathVariable Long eventId,
            @Valid @RequestBody TicketRequest request) {
        TicketResponse response = ticketService.createTicket(eventId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // GET /api/events/{eventId}/tickets
    @GetMapping("/api/events/{eventId}/tickets")
    public ResponseEntity<List<TicketResponse>> getTicketsByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(ticketService.getTicketsByEvent(eventId));
    }

    // GET /api/users/{userId}/tickets
    @GetMapping("/api/users/{userId}/tickets")
    public ResponseEntity<List<TicketResponse>> getTicketsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ticketService.getTicketsByUser(userId));
    }

    // GET /api/tickets/{id}
    @GetMapping("/api/tickets/{id}")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    // PUT /api/tickets/{id}/check-in
    @PutMapping("/api/tickets/{id}/check-in")
    public ResponseEntity<TicketResponse> checkIn(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.checkIn(id));
    }

    // DELETE /api/tickets/{id}
    @DeleteMapping("/api/tickets/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }
}
