package tn.esprit.esprit_market.modules.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.event.dto.EventRequest;
import tn.esprit.esprit_market.modules.event.dto.EventResponse;
import tn.esprit.esprit_market.modules.event.dto.EventStatisticsDTO;
import tn.esprit.esprit_market.modules.event.enums.EventStatus;
import tn.esprit.esprit_market.modules.event.enums.EventType;
import tn.esprit.esprit_market.modules.event.service.IEventService;
import tn.esprit.esprit_market.modules.user.enums.Role;

import java.util.List;

import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final IEventService eventService;

    // POST /api/events
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventRequest request,
            Authentication authentication) {
        EventResponse response = eventService.createEvent(request, authentication.getName());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // GET /api/events
    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {

        return ResponseEntity.ok(eventService.getAllEvents());
    }

    // GET /api/events/{id}
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    // GET /api/events/type/{type}
    @GetMapping("/type/{type}")
    public ResponseEntity<List<EventResponse>> getEventsByType(@PathVariable EventType type) {
        return ResponseEntity.ok(eventService.getEventsByType(type));
    }

    // GET /api/events/store/{storeId}
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<EventResponse>> getEventsByStore(@PathVariable Long storeId) {
        return ResponseEntity.ok(eventService.getEventsByStore(storeId));
    }

    // GET /api/events/service/{serviceId}
    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<EventResponse>> getEventsByService(@PathVariable Long serviceId) {
        return ResponseEntity.ok(eventService.getEventsByService(serviceId));
    }

    // PUT /api/events/{id}
    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable Long id,
            @Valid @RequestBody EventRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(eventService.updateEvent(id, request, authentication.getName()));
    }

    // PUT /api/events/{id}/status
    @PutMapping("/{id}/status")
    public ResponseEntity<EventResponse> updateEventStatus(@PathVariable Long id, @RequestParam EventStatus status,
            Authentication authentication) {
        return ResponseEntity.ok(eventService.updateEventStatus(id, status, authentication.getName()));
    }

    // DELETE /api/events/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id, Authentication authentication) {
        eventService.deleteEvent(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    // =====================================================================
    // JPQL : statistiques par organisateur (JOIN Event + User + Ticket)
    // GET /api/events/statistics/organizer/{organizerId}
    // =====================================================================
    @GetMapping("/statistics/organizer/{organizerId}")
    public ResponseEntity<List<EventStatisticsDTO>> getEventStatistics(@PathVariable Long organizerId) {
        return ResponseEntity.ok(eventService.getEventStatisticsByOrganizer(organizerId));
    }

    // GET /api/events/statistics/my-stats
    @GetMapping("/statistics/my-stats")
    public ResponseEntity<List<EventStatisticsDTO>> getMyEventStatistics(Authentication authentication) {
        return ResponseEntity.ok(eventService.getMyEventStatistics(authentication.getName()));
    }

    // =====================================================================
    // NOUVEAUX KEYWORDS : Recherche pour les événements d'un Seller
    // GET /api/events/search/my-events-by-title?title=xxx
    // =====================================================================
    @GetMapping("/search/my-events-by-title")
    public ResponseEntity<List<EventResponse>> searchMyEventsByTitle(
            @RequestParam String title,
            Authentication authentication) {
        return ResponseEntity.ok(eventService.searchMyEventsByTitle(authentication.getName(), title));
    }

    // =====================================================================
    // NOUVEAUX KEYWORDS : Recherche pour les événements d'un Seller
    // GET /api/events/search/my-events-after-date?date=yyyy-MM-dd
    // =====================================================================
    @GetMapping("/search/my-events-after-date")
    public ResponseEntity<List<EventResponse>> searchMyEventsCreatedAfter(
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(pattern="yyyy-MM-dd") java.util.Date date,
            Authentication authentication) {
        return ResponseEntity.ok(eventService.searchMyEventsCreatedAfter(authentication.getName(), date));
    }
}

