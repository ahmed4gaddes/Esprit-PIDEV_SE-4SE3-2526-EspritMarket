package tn.esprit.esprit_market.modules.event.service;

import tn.esprit.esprit_market.modules.event.dto.EventRequest;
import tn.esprit.esprit_market.modules.event.dto.EventResponse;
import tn.esprit.esprit_market.modules.event.dto.EventStatisticsDTO;
import tn.esprit.esprit_market.modules.event.enums.EventStatus;
import tn.esprit.esprit_market.modules.event.enums.EventType;
import tn.esprit.esprit_market.modules.user.enums.Role;

import java.util.List;

public interface IEventService {
    EventResponse createEvent(EventRequest request, String userEmail);
    List<EventResponse> getAllEvents();
    EventResponse getEventById(Long id);
    List<EventResponse> getEventsByType(EventType type);
    List<EventResponse> getEventsByStore(Long storeId);
    List<EventResponse> getEventsByService(Long serviceId);
    EventResponse updateEvent(Long id, EventRequest request, String userEmail);
    EventResponse updateEventStatus(Long id, EventStatus status, String userEmail);
    void deleteEvent(Long id, String userEmail);

    // JPQL : statistiques par organisateur
    List<EventStatisticsDTO> getEventStatisticsByOrganizer(Long organizerId);
    List<EventStatisticsDTO> getMyEventStatistics(String userEmail);

    // Keywords : recherche par rôle organisateur + statut
    List<EventResponse> getEventsByOrganizerRoleAndStatus(Role role, EventStatus status);

    // Keywords : recherche par nom de store + type
    List<EventResponse> getEventsByStoreNameAndType(String storeName, EventType type);

    // Keywords : events à venir d'un organisateur
    List<EventResponse> getUpcomingEventsByOrganizer(Long organizerId);
}

