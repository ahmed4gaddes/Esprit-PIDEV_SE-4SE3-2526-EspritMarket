package tn.esprit.esprit_market.modules.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.event.dto.LiveSessionRequest;
import tn.esprit.esprit_market.modules.event.dto.LiveSessionResponse;
import tn.esprit.esprit_market.modules.event.service.LiveSessionService;

@RestController
@RequiredArgsConstructor
public class LiveSessionController {

    private final LiveSessionService liveSessionService;

    // POST /api/events/{eventId}/live-session
    @PostMapping("/api/events/{eventId}/live-session")
    public ResponseEntity<LiveSessionResponse> createLiveSession(@PathVariable Long eventId,
            @Valid @RequestBody LiveSessionRequest request) {
        LiveSessionResponse response = liveSessionService.createLiveSession(eventId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // GET /api/events/{eventId}/live-session
    @GetMapping("/api/events/{eventId}/live-session")
    public ResponseEntity<LiveSessionResponse> getLiveSessionByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(liveSessionService.getLiveSessionByEvent(eventId));
    }

    // PUT /api/live-sessions/{id}
    @PutMapping("/api/live-sessions/{id}")
    public ResponseEntity<LiveSessionResponse> updateLiveSession(@PathVariable Long id,
            @Valid @RequestBody LiveSessionRequest request) {
        return ResponseEntity.ok(liveSessionService.updateLiveSession(id, request));
    }

    // DELETE /api/live-sessions/{id}
    @DeleteMapping("/api/live-sessions/{id}")
    public ResponseEntity<Void> deleteLiveSession(@PathVariable Long id) {
        liveSessionService.deleteLiveSession(id);
        return ResponseEntity.noContent().build();
    }
}
