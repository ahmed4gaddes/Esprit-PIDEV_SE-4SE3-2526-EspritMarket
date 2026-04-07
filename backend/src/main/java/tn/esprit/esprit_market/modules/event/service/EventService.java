package tn.esprit.esprit_market.modules.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.event.dto.EventRequest;
import tn.esprit.esprit_market.modules.event.dto.EventResponse;
import tn.esprit.esprit_market.modules.event.dto.EventStatisticsDTO;
import tn.esprit.esprit_market.modules.event.entities.Event;
import tn.esprit.esprit_market.modules.event.entities.LiveSession;
import tn.esprit.esprit_market.modules.event.entities.Ticket;
import tn.esprit.esprit_market.modules.event.enums.EventStatus;
import tn.esprit.esprit_market.modules.event.enums.EventType;
import tn.esprit.esprit_market.modules.event.enums.LiveSessionStatus;
import tn.esprit.esprit_market.modules.event.enums.TicketStatus;
import tn.esprit.esprit_market.modules.event.repositories.EventRepository;
import tn.esprit.esprit_market.modules.event.repositories.LiveSessionRepository;
import tn.esprit.esprit_market.modules.event.repositories.TicketRepository;
import tn.esprit.esprit_market.modules.store.entity.Store;
import tn.esprit.esprit_market.modules.store.service.IserviceStore;
import tn.esprit.esprit_market.modules.service.service.IServiceService;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.enums.Role;
import tn.esprit.esprit_market.modules.user.service.IUserService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService implements IEventService {

    private static final String EVENT_NOT_FOUND_MSG = "Event not found with id: ";

    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final LiveSessionRepository liveSessionRepository;
    private final IUserService userService;
    private final IserviceStore iserviceStore;
    private final IServiceService iserviceService;

    // =====================================================================
    // SCHEDULER : archivage automatique des events expirés (toutes les 30 min)
    // =====================================================================
    @Scheduled(fixedRate = 100000) // 30 minutes en millisecondes
    public void archiveExpiredEvents() {
        log.info("=== Scheduler: Début de l'archivage des événements expirés ===");

        Date now = new Date();

        // 1. Chercher les events dont la date est passée et qui sont encore UPCOMING ou ONGOING
        List<Event> expiredEvents = eventRepository.findByDateBeforeAndStatusIn(
                now, List.of(EventStatus.UPCOMING, EventStatus.ONGOING));

        if (expiredEvents.isEmpty()) {
            log.info("Scheduler: Aucun événement expiré trouvé.");
            return;
        }

        // 2. Récupérer les IDs des events expirés
        List<Long> expiredEventIds = expiredEvents.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        // 3. Mettre à jour le statut des events → COMPLETED
        for (Event event : expiredEvents) {
            event.setStatus(EventStatus.COMPLETED);
        }
        eventRepository.saveAll(expiredEvents);
        log.info("Scheduler: {} événement(s) passé(s) en COMPLETED.", expiredEvents.size());

        // 4. Expirer les tickets VALID de ces events → EXPIRED
        List<Ticket> ticketsToExpire = ticketRepository.findByEventIdInAndStatus(
                expiredEventIds, TicketStatus.VALID);
        for (Ticket ticket : ticketsToExpire) {
            ticket.setStatus(TicketStatus.EXPIRED);
        }
        ticketRepository.saveAll(ticketsToExpire);
        log.info("Scheduler: {} ticket(s) passé(s) en EXPIRED.", ticketsToExpire.size());

        // 5. Fermer les LiveSessions encore actives → ENDED
        List<LiveSession> livesToClose = liveSessionRepository.findByEventIdInAndStatusIn(
                expiredEventIds, List.of(LiveSessionStatus.SCHEDULED, LiveSessionStatus.LIVE));
        for (LiveSession live : livesToClose) {
            live.setStatus(LiveSessionStatus.ENDED);
        }
        liveSessionRepository.saveAll(livesToClose);
        log.info("Scheduler: {} live session(s) passée(s) en ENDED.", livesToClose.size());

        log.info("=== Scheduler: Archivage terminé ===");
    }

    // =====================================================================
    // JPQL : statistiques par organisateur
    // =====================================================================
    @Override
    public List<EventStatisticsDTO> getEventStatisticsByOrganizer(Long organizerId) {
        return eventRepository.findEventStatisticsByOrganizer(organizerId);
    }

    // =====================================================================
    // KEYWORDS : recherche par rôle de l'organisateur + statut
    // =====================================================================
    @Override
    public List<EventResponse> getEventsByOrganizerRoleAndStatus(Role role, EventStatus status) {
        return eventRepository.findByOrganizerRoleAndStatus(role, status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================================
    // KEYWORDS : recherche par nom de store + type
    // =====================================================================
    @Override
    public List<EventResponse> getEventsByStoreNameAndType(String storeName, EventType type) {
        return eventRepository.findByStoreNameContainingIgnoreCaseAndType(storeName, type)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================================
    // KEYWORDS : events à venir d'un organisateur
    // =====================================================================
    @Override
    public List<EventResponse> getUpcomingEventsByOrganizer(Long organizerId) {
        return eventRepository.findByOrganizerIdAndStatusAndDateAfter(
                        organizerId, EventStatus.UPCOMING, new Date())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ==================== CREATE ====================
    public EventResponse createEvent(EventRequest request, String userEmail) {
        // The organizer is the authenticated user
        User organizer = userService.getUserByEmail(userEmail);

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
                .organizer(organizer)
                .build();

        // Associer le Store si fourni (pour les Sellers)
        if (request.getStoreId() != null) {
            Store store = iserviceStore.getStoreById(request.getStoreId());
            event.setStore(store);
        }

        // Associer le Service si fourni (pour Workshops, Certificates, etc.)
        if (request.getServiceId() != null) {
            tn.esprit.esprit_market.modules.service.entity.Service service = iserviceService.getEntityById(request.getServiceId());
            event.setService(service);
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

    // ==================== READ BY STORE ====================
    public List<EventResponse> getEventsByStore(Long storeId) {
        return eventRepository.findByStoreId(storeId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ==================== READ BY SERVICE ====================
    public List<EventResponse> getEventsByService(Long serviceId) {
        return eventRepository.findByServiceId(serviceId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ==================== UPDATE ====================
    public EventResponse updateEvent(Long id, EventRequest request, String userEmail) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(EVENT_NOT_FOUND_MSG + id));

        // Ownership check: only the organizer or admin can update
        verifyOwnership(event, userEmail);

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setDate(request.getDate());
        event.setLocation(request.getLocation());
        event.setImageUrl(request.getImageUrl());
        event.setTicketPrice(request.getTicketPrice());
        event.setCapacity(request.getCapacity());
        event.setType(request.getType());

        // Mettre à jour le Store si changé
        if (request.getStoreId() != null) {
            Store store = iserviceStore.getStoreById(request.getStoreId());
            event.setStore(store);
        } else {
            event.setStore(null);
        }

        // Mettre à jour le Service si changé
        if (request.getServiceId() != null) {
            tn.esprit.esprit_market.modules.service.entity.Service service = iserviceService.getEntityById(request.getServiceId());
            event.setService(service);
        } else {
            event.setService(null);
        }

        Event updatedEvent = eventRepository.save(event);
        return mapToResponse(updatedEvent);
    }

    public EventResponse updateEventStatus(Long id, EventStatus status, String userEmail) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(EVENT_NOT_FOUND_MSG + id));
        verifyOwnership(event, userEmail);
        event.setStatus(status);
        return mapToResponse(eventRepository.save(event));
    }

    // ==================== DELETE ====================
    public void deleteEvent(Long id, String userEmail) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(EVENT_NOT_FOUND_MSG + id));
        verifyOwnership(event, userEmail);
        eventRepository.delete(event);
    }

    // ==================== OWNERSHIP CHECK ====================
    private void verifyOwnership(Event event, String userEmail) {
        User currentUser = userService.getUserByEmail(userEmail);
        // Admin can do anything
        if ("ADMIN".equals(currentUser.getRole().name())) {
            return;
        }
        // Check if the current user is the organizer
        if (event.getOrganizer() == null || !event.getOrganizer().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à modifier cet événement.");
        }
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
                .storeId(event.getStore() != null ? event.getStore().getId() : null)
                .storeName(event.getStore() != null ? event.getStore().getName() : null)
                .serviceId(event.getService() != null ? event.getService().getId() : null)
                .serviceTitle(event.getService() != null ? event.getService().getTitle() : null)
                .build();
    }
}

