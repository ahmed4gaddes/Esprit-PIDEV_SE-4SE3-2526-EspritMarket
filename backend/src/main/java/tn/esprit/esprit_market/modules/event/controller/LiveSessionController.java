package tn.esprit.esprit_market.modules.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.event.dto.LiveSessionRequest;
import tn.esprit.esprit_market.modules.event.dto.LiveSessionResponse;
import tn.esprit.esprit_market.modules.event.enums.LiveSessionStatus;
import tn.esprit.esprit_market.modules.event.service.ILiveSessionService;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.service.IUserService;

import java.util.List;

@RestController
@RequestMapping("/api/live-sessions")
@RequiredArgsConstructor
public class LiveSessionController {

    private final ILiveSessionService liveSessionService;
    private final IUserService userService;

    // POST /api/live-sessions
    @PostMapping
    public ResponseEntity<LiveSessionResponse> createLiveSession(
            @RequestParam(required = false) Long eventId,
            @Valid @RequestBody LiveSessionRequest request,
            Authentication authentication) {

        String userEmail = authentication.getName();
        User user = userService.getUserByEmail(userEmail);

        LiveSessionResponse response = liveSessionService.createLiveSession(user.getId(), eventId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // GET /api/live-sessions
    @GetMapping
    public ResponseEntity<List<LiveSessionResponse>> getAllLiveSessions() {
        return ResponseEntity.ok(liveSessionService.getAllLiveSessions());
    }

    // GET /api/live-sessions/event/{eventId}
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<LiveSessionResponse>> getLiveSessionsByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(liveSessionService.getLiveSessionsByEvent(eventId));
    }

    // GET /api/live-sessions/store/{storeId}
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<LiveSessionResponse>> getLiveSessionsByStore(@PathVariable Long storeId) {
        return ResponseEntity.ok(liveSessionService.getLiveSessionsByStore(storeId));
    }

    // GET /api/live-sessions/service/{serviceId}
    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<LiveSessionResponse>> getLiveSessionsByService(@PathVariable Long serviceId) {
        return ResponseEntity.ok(liveSessionService.getLiveSessionsByService(serviceId));
    }

    // GET /api/live-sessions/user/{userId}
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LiveSessionResponse>> getLiveSessionsByCreator(@PathVariable Long userId) {
        return ResponseEntity.ok(liveSessionService.getLiveSessionsByCreator(userId));
    }

    // GET /api/live-sessions/{id}
    @GetMapping("/{id}")
    public ResponseEntity<LiveSessionResponse> getLiveSessionById(@PathVariable Long id) {
        return ResponseEntity.ok(liveSessionService.getLiveSessionById(id));
    }

    // PUT /api/live-sessions/{id}
    @PutMapping("/{id}")
    public ResponseEntity<LiveSessionResponse> updateLiveSession(@PathVariable Long id,
            @Valid @RequestBody LiveSessionRequest request) {
        return ResponseEntity.ok(liveSessionService.updateLiveSession(id, request));
    }

    // PUT /api/live-sessions/{id}/status
    @PutMapping("/{id}/status")
    public ResponseEntity<LiveSessionResponse> updateStatus(@PathVariable Long id,
            @RequestParam LiveSessionStatus status) {
        return ResponseEntity.ok(liveSessionService.updateStatus(id, status));
    }

    // DELETE /api/live-sessions/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLiveSession(@PathVariable Long id) {
        liveSessionService.deleteLiveSession(id);
        return ResponseEntity.noContent().build();
    }
}
