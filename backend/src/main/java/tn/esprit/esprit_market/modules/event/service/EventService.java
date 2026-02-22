package tn.esprit.esprit_market.modules.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.event.dto.EventRequest;
import tn.esprit.esprit_market.modules.event.dto.EventResponse;
import tn.esprit.esprit_market.modules.event.entities.Event;
import tn.esprit.esprit_market.modules.event.enums.EventType;
import tn.esprit.esprit_market.modules.event.repositories.EventRepository;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    // ==================== CREATE ====================
    public EventResponse createEvent(EventRequest request) {
        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .date(request.getDate())
                .capacity(request.getCapacity())
                .type(request.getType())
                .build();

        // Associer l'organisateur si fourni
        if (request.getOrganizerId() != null) {
            User organizer = userRepository.findById(request.getOrganizerId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User not found with id: " + request.getOrganizerId()));
            event.setOrganizer(organizer);
        }

        Event savedEvent = eventRepository.save(event);
        return mapToResponse(savedEvent);
    }

    // ==================== READ ALL ====================
    public List<EventResponse> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ==================== READ ONE ====================
    public EventResponse getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        return mapToResponse(event);
    }

    // ==================== READ BY TYPE ====================
    public List<EventResponse> getEventsByType(EventType type) {
        return eventRepository.findByType(type)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ==================== UPDATE ====================
    public EventResponse updateEvent(Long id, EventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setDate(request.getDate());
        event.setCapacity(request.getCapacity());
        event.setType(request.getType());

        // Mettre à jour l'organisateur si changé
        if (request.getOrganizerId() != null) {
            User organizer = userRepository.findById(request.getOrganizerId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User not found with id: " + request.getOrganizerId()));
            event.setOrganizer(organizer);
        }

        Event updatedEvent = eventRepository.save(event);
        return mapToResponse(updatedEvent);
    }

    // ==================== DELETE ====================
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        eventRepository.delete(event);
    }

    // ==================== MAPPER ====================
    private EventResponse mapToResponse(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .date(event.getDate())
                .capacity(event.getCapacity())
                .type(event.getType())
                .organizerName(event.getOrganizer() != null ? event.getOrganizer().getName() : null)
                .createdAt(event.getCreatedAt())
                .build();
    }
}
