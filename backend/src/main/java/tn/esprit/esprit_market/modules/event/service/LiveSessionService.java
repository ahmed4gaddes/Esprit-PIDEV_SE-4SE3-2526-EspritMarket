package tn.esprit.esprit_market.modules.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.event.dto.LiveSessionRequest;
import tn.esprit.esprit_market.modules.event.dto.LiveSessionResponse;
import tn.esprit.esprit_market.modules.event.entities.Event;
import tn.esprit.esprit_market.modules.event.entities.LiveSession;
import tn.esprit.esprit_market.modules.event.repositories.EventRepository;
import tn.esprit.esprit_market.modules.event.repositories.LiveSessionRepository;

@Service
@RequiredArgsConstructor
public class LiveSessionService {

    private final LiveSessionRepository liveSessionRepository;
    private final EventRepository eventRepository;

    // ==================== CREATE ====================
    public LiveSessionResponse createLiveSession(Long eventId, LiveSessionRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));

        // Vérifier qu'il n'y a pas déjà une session pour cet event
        if (liveSessionRepository.findByEventId(eventId).isPresent()) {
            throw new IllegalStateException("A live session already exists for this event");
        }

        LiveSession liveSession = LiveSession.builder()
                .link(request.getLink())
                .platform(request.getPlatform())
                .startTime(request.getStartTime())
                .event(event)
                .build();

        LiveSession savedSession = liveSessionRepository.save(liveSession);
        return mapToResponse(savedSession);
    }

    // ==================== READ BY EVENT ====================
    public LiveSessionResponse getLiveSessionByEvent(Long eventId) {
        LiveSession liveSession = liveSessionRepository.findByEventId(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Live session not found for event id: " + eventId));
        return mapToResponse(liveSession);
    }

    // ==================== UPDATE ====================
    public LiveSessionResponse updateLiveSession(Long id, LiveSessionRequest request) {
        LiveSession liveSession = liveSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Live session not found with id: " + id));

        liveSession.setLink(request.getLink());
        liveSession.setPlatform(request.getPlatform());
        liveSession.setStartTime(request.getStartTime());

        LiveSession updatedSession = liveSessionRepository.save(liveSession);
        return mapToResponse(updatedSession);
    }

    // ==================== DELETE ====================
    public void deleteLiveSession(Long id) {
        LiveSession liveSession = liveSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Live session not found with id: " + id));
        liveSessionRepository.delete(liveSession);
    }

    // ==================== MAPPER ====================
    private LiveSessionResponse mapToResponse(LiveSession liveSession) {
        return LiveSessionResponse.builder()
                .id(liveSession.getId())
                .link(liveSession.getLink())
                .platform(liveSession.getPlatform())
                .startTime(liveSession.getStartTime())
                .eventId(liveSession.getEvent() != null ? liveSession.getEvent().getId() : null)
                .eventTitle(liveSession.getEvent() != null ? liveSession.getEvent().getTitle() : null)
                .build();
    }
}
