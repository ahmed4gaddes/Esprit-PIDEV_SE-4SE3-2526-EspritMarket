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
import tn.esprit.esprit_market.modules.service.service.IServiceService;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.enums.Role;
import tn.esprit.esprit_market.modules.user.service.IUserService;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private IUserService userService;

    @Mock
    private IserviceStore iserviceStore;

    @Mock
    private IServiceService iserviceService;

    @InjectMocks
    private EventService eventService;

    private Event fakeEvent;
    private EventRequest fakeRequest;
    private User fakeUser;
    private static final String USER_EMAIL = "ahmed@esprit.tn";

    @BeforeEach
    void setUp() {
        // Fake user (owner)
        fakeUser = new User();
        fakeUser.setId(1L);
        fakeUser.setEmail(USER_EMAIL);
        fakeUser.setRole(Role.SELLER);

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
                .organizer(fakeUser)
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
    void testCreateEvent_Success() {
        // ARRANGE
        when(userService.getUserByEmail(USER_EMAIL)).thenReturn(fakeUser);
        when(eventRepository.save(any(Event.class))).thenReturn(fakeEvent);

        // ACT
        EventResponse result = eventService.createEvent(fakeRequest, USER_EMAIL);

        // ASSERT
        assertNotNull(result);
        assertEquals("Festival ESPRIT", result.getTitle());
        assertEquals(EventStatus.UPCOMING, result.getStatus());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void testCreateEvent_WithInvalidUserEmail_ThrowsException() {
        // ARRANGE — User email not found
        when(userService.getUserByEmail("unknown@esprit.tn")).thenThrow(
                new ResourceNotFoundException("User not found"));

        // ASSERT — On s'attend à une ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> eventService.createEvent(fakeRequest, "unknown@esprit.tn"));
        verify(eventRepository, never()).save(any()); // save() ne doit JAMAIS être appelé
    }

    @Test
    void testCreateEvent_WithInvalidStoreId_ThrowsException() {
        // ARRANGE — Store inexistant
        fakeRequest.setStoreId(999L);
        when(userService.getUserByEmail(USER_EMAIL)).thenReturn(fakeUser);
        when(iserviceStore.getStoreById(999L)).thenThrow(
                new ResourceNotFoundException("Store not found with id: 999"));

        // ASSERT
        assertThrows(ResourceNotFoundException.class, () -> eventService.createEvent(fakeRequest, USER_EMAIL));
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
        when(userService.getUserByEmail(USER_EMAIL)).thenReturn(fakeUser);
        when(eventRepository.save(any(Event.class))).thenReturn(fakeEvent);

        // ACT
        EventResponse result = eventService.updateEvent(1L, fakeRequest, USER_EMAIL);

        // ASSERT
        assertNotNull(result);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void testUpdateEvent_NotFound_ThrowsException() {
        // ARRANGE
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        // ASSERT
        assertThrows(ResourceNotFoundException.class, () -> eventService.updateEvent(99L, fakeRequest, USER_EMAIL));
    }

    // ==================== deleteEvent ====================
    @Test
    void testDeleteEvent_Success() {
        // ARRANGE
        when(eventRepository.findById(1L)).thenReturn(Optional.of(fakeEvent));
        when(userService.getUserByEmail(USER_EMAIL)).thenReturn(fakeUser);
        doNothing().when(eventRepository).delete(fakeEvent);

        // ACT
        eventService.deleteEvent(1L, USER_EMAIL);

        // ASSERT
        verify(eventRepository, times(1)).delete(fakeEvent);
    }

    @Test
    void testDeleteEvent_NotFound_ThrowsException() {
        // ARRANGE
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        // ASSERT
        assertThrows(ResourceNotFoundException.class, () -> eventService.deleteEvent(99L, USER_EMAIL));
    }

    // ==================== updateEventStatus ====================
    @Test
    void testUpdateEventStatus_Success() {
        // ARRANGE
        when(eventRepository.findById(1L)).thenReturn(Optional.of(fakeEvent));
        when(userService.getUserByEmail(USER_EMAIL)).thenReturn(fakeUser);
        fakeEvent.setStatus(EventStatus.ONGOING);
        when(eventRepository.save(fakeEvent)).thenReturn(fakeEvent);

        // ACT
        EventResponse result = eventService.updateEventStatus(1L, EventStatus.ONGOING, USER_EMAIL);

        // ASSERT
        assertNotNull(result);
        assertEquals(EventStatus.ONGOING, result.getStatus());
    }
}
