package tn.esprit.esprit_market.modules.event.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.event.dto.EventRequest;
import tn.esprit.esprit_market.modules.event.dto.EventResponse;
import tn.esprit.esprit_market.modules.event.entities.Event;
import tn.esprit.esprit_market.modules.event.enums.EventStatus;
import tn.esprit.esprit_market.modules.event.enums.EventType;
import tn.esprit.esprit_market.modules.event.repositories.EventRepository;
import tn.esprit.esprit_market.modules.store.service.IserviceStore;
import tn.esprit.esprit_market.modules.user.service.IUserService;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private IUserService userService;

    @Mock
    private IserviceStore iserviceStore;

    @InjectMocks
    private EventService eventService;

    private Event fakeEvent;
    private EventRequest fakeRequest;

    @BeforeEach
    void setUp() {
        // Préparer un Event de test
        fakeEvent = Event.builder()
                .id(1L)
                .title("Festival ESPRIT")
                .description("Grand festival annuel")
                .date(new Date())
                .location("Campus ESPRIT")
                .capacity(200)
                .ticketPrice(15.0)
                .type(EventType.PRODUCT_LAUNCH_EVENT)
                .status(EventStatus.UPCOMING)
                .build();

        // Préparer une requête de création
        fakeRequest = EventRequest.builder()
                .title("Festival ESPRIT")
                .description("Grand festival annuel")
                .date(new Date())
                .location("Campus ESPRIT")
                .capacity(200)
                .ticketPrice(15.0)
                .type(EventType.PRODUCT_LAUNCH_EVENT)
                .build();
    }

    // ==================== createEvent ====================
    @Test
    void testCreateEvent_WithoutOrganizerOrStore_Success() {
        // ARRANGE
        when(eventRepository.save(any(Event.class))).thenReturn(fakeEvent);

        // ACT
        EventResponse result = eventService.createEvent(fakeRequest);

        // ASSERT
        assertNotNull(result);
        assertEquals("Festival ESPRIT", result.getTitle());
        assertEquals(EventStatus.UPCOMING, result.getStatus()); // Le statut doit toujours commencer par UPCOMING
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void testCreateEvent_WithInvalidOrganizerId_ThrowsException() {
        // ARRANGE — Organisateur inexistant
        fakeRequest.setOrganizerId(999L);
        when(userService.getUserById(999L)).thenThrow(
                new ResourceNotFoundException("User not found with id: 999"));

        // ASSERT — On s'attend à une ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> eventService.createEvent(fakeRequest));
        verify(eventRepository, never()).save(any()); // save() ne doit JAMAIS être appelé
    }

    @Test
    void testCreateEvent_WithInvalidStoreId_ThrowsException() {
        // ARRANGE — Store inexistant
        fakeRequest.setStoreId(999L);
        when(iserviceStore.getStoreById(999L)).thenThrow(
                new ResourceNotFoundException("Store not found with id: 999"));

        // ASSERT
        assertThrows(ResourceNotFoundException.class, () -> eventService.createEvent(fakeRequest));
        verify(eventRepository, never()).save(any());
    }

    // ==================== getAllEvents ====================
    @Test
    void testGetAllEvents_ReturnsList() {
        // ARRANGE
        Event event2 = Event.builder()
                .id(2L).title("Conférence Tech").status(EventStatus.UPCOMING).build();
        when(eventRepository.findAll()).thenReturn(Arrays.asList(fakeEvent, event2));

        // ACT
        List<EventResponse> result = eventService.getAllEvents();

        // ASSERT
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Festival ESPRIT", result.get(0).getTitle());
    }

    // ==================== getEventById ====================
    @Test
    void testGetEventById_Found() {
        // ARRANGE
        when(eventRepository.findById(1L)).thenReturn(Optional.of(fakeEvent));

        // ACT
        EventResponse result = eventService.getEventById(1L);

        // ASSERT
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Festival ESPRIT", result.getTitle());
    }

    @Test
    void testGetEventById_NotFound_ThrowsException() {
        // ARRANGE
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        // ASSERT
        assertThrows(ResourceNotFoundException.class, () -> eventService.getEventById(99L));
    }

    // ==================== updateEvent ====================
    @Test
    void testUpdateEvent_Success() {
        // ARRANGE
        fakeRequest.setTitle("Festival ESPRIT 2026");
        when(eventRepository.findById(1L)).thenReturn(Optional.of(fakeEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(fakeEvent);

        // ACT
        EventResponse result = eventService.updateEvent(1L, fakeRequest);

        // ASSERT
        assertNotNull(result);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void testUpdateEvent_NotFound_ThrowsException() {
        // ARRANGE
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        // ASSERT
        assertThrows(ResourceNotFoundException.class, () -> eventService.updateEvent(99L, fakeRequest));
    }

    // ==================== deleteEvent ====================
    @Test
    void testDeleteEvent_Success() {
        // ARRANGE
        when(eventRepository.findById(1L)).thenReturn(Optional.of(fakeEvent));
        doNothing().when(eventRepository).delete(fakeEvent);

        // ACT
        eventService.deleteEvent(1L);

        // ASSERT
        verify(eventRepository, times(1)).delete(fakeEvent);
    }

    @Test
    void testDeleteEvent_NotFound_ThrowsException() {
        // ARRANGE
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        // ASSERT
        assertThrows(ResourceNotFoundException.class, () -> eventService.deleteEvent(99L));
    }

    // ==================== updateEventStatus ====================
    @Test
    void testUpdateEventStatus_Success() {
        // ARRANGE
        when(eventRepository.findById(1L)).thenReturn(Optional.of(fakeEvent));
        fakeEvent.setStatus(EventStatus.ONGOING);
        when(eventRepository.save(fakeEvent)).thenReturn(fakeEvent);

        // ACT
        EventResponse result = eventService.updateEventStatus(1L, EventStatus.ONGOING);

        // ASSERT
        assertNotNull(result);
        assertEquals(EventStatus.ONGOING, result.getStatus());
    }
}
