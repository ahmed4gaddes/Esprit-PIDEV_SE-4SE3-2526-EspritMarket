package tn.esprit.esprit_market.modules.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.event.dto.EventRequest;
import tn.esprit.esprit_market.modules.event.dto.EventResponse;
import tn.esprit.esprit_market.modules.event.entities.Event;
import tn.esprit.esprit_market.modules.event.enums.EventStatus;
import tn.esprit.esprit_market.modules.event.enums.EventType;
import tn.esprit.esprit_market.modules.event.repositories.EventRepository;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private static final String EVENT_NOT_FOUND_MSG = "Event not found with id: ";

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    // ==================== CREATE ====================
    public EventResponse createEvent(EventRequest request) {
        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .date(request.getDate())
                .location(request.getLocation())
                .imageUrl(request.getImageUrl())
                .ticketPrice(request.getTicketPrice())
                .capacity(request.getCapacity())
                .type(request.getType())
                .status(EventStatus.UPCOMING)
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
                .toList();
    }

    // ==================== READ ONE ====================
    public EventResponse getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(EVENT_NOT_FOUND_MSG + id));
        return mapToResponse(event);
    }

    // ==================== READ BY TYPE ====================
    public List<EventResponse> getEventsByType(EventType type) {
        return eventRepository.findByType(type)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ==================== UPDATE ====================
    public EventResponse updateEvent(Long id, EventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(EVENT_NOT_FOUND_MSG + id));

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setDate(request.getDate());
        event.setLocation(request.getLocation());
        event.setImageUrl(request.getImageUrl());
        event.setTicketPrice(request.getTicketPrice());
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

    public EventResponse updateEventStatus(Long id, EventStatus status) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(EVENT_NOT_FOUND_MSG + id));
        event.setStatus(status);
        return mapToResponse(eventRepository.save(event));
    }

    // ==================== DELETE ====================
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(EVENT_NOT_FOUND_MSG + id));
        eventRepository.delete(event);
    }

    // ==================== MAPPER ====================
    private EventResponse mapToResponse(Event event) {
        int ticketCount = event.getTickets() != null ? event.getTickets().size() : 0;

        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .date(event.getDate())
                .location(event.getLocation())
                .imageUrl(event.getImageUrl())
                .status(event.getStatus())
                .ticketPrice(event.getTicketPrice())
                .ticketCount(ticketCount)
                .capacity(event.getCapacity())
                .type(event.getType())
                .organizerName(event.getOrganizer() != null ? event.getOrganizer().getName() : null)
                .createdAt(event.getCreatedAt())
                .build();
    }
}
