package tn.esprit.esprit_market.modules.event.service;

import tn.esprit.esprit_market.modules.event.dto.EventRequest;
import tn.esprit.esprit_market.modules.event.dto.EventResponse;
import tn.esprit.esprit_market.modules.event.enums.EventStatus;
import tn.esprit.esprit_market.modules.event.enums.EventType;

import java.util.List;

public interface IEventService {
    EventResponse createEvent(EventRequest request);
    List<EventResponse> getAllEvents();
    EventResponse getEventById(Long id);
    List<EventResponse> getEventsByType(EventType type);
    List<EventResponse> getEventsByStore(Long storeId);
    List<EventResponse> getEventsByService(Long serviceId);
    EventResponse updateEvent(Long id, EventRequest request);
    EventResponse updateEventStatus(Long id, EventStatus status);
    void deleteEvent(Long id);
}
