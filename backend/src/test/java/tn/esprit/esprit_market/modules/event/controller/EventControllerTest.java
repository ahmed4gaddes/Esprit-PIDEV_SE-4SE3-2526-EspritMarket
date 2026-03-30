package tn.esprit.esprit_market.modules.event.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import tn.esprit.esprit_market.modules.event.dto.EventRequest;
import tn.esprit.esprit_market.modules.event.dto.EventResponse;
import tn.esprit.esprit_market.modules.event.enums.EventStatus;
import tn.esprit.esprit_market.modules.event.enums.EventType;
import tn.esprit.esprit_market.modules.event.service.IEventService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock
    private IEventService eventService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private EventController eventController;

    private EventRequest request;
    private EventResponse responseDto;

    @BeforeEach
    void setUp() {
        request = new EventRequest();
        request.setTitle("My Event");

        responseDto = new EventResponse();
        responseDto.setId(1L);
        responseDto.setTitle("My Event");
    }

    @Test
    void testCreateEvent() {
        when(authentication.getName()).thenReturn("user@esprit.tn");
        when(eventService.createEvent(any(EventRequest.class), eq("user@esprit.tn"))).thenReturn(responseDto);

        ResponseEntity<EventResponse> response = eventController.createEvent(request, authentication);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(eventService).createEvent(any(EventRequest.class), eq("user@esprit.tn"));
    }

    @Test
    void testGetAllEvents() {
        when(eventService.getAllEvents()).thenReturn(Arrays.asList(responseDto));

        ResponseEntity<List<EventResponse>> response = eventController.getAllEvents();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(eventService).getAllEvents();
    }

    @Test
    void testGetEventById() {
        when(eventService.getEventById(1L)).thenReturn(responseDto);
        ResponseEntity<EventResponse> response = eventController.getEventById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void testGetEventsByType() {
        when(eventService.getEventsByType(EventType.WORKSHOP_EVENT)).thenReturn(Arrays.asList(responseDto));
        ResponseEntity<List<EventResponse>> response = eventController.getEventsByType(EventType.WORKSHOP_EVENT);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(eventService).getEventsByType(EventType.WORKSHOP_EVENT);
    }

    @Test
    void testGetEventsByStore() {
        when(eventService.getEventsByStore(5L)).thenReturn(Arrays.asList(responseDto));
        ResponseEntity<List<EventResponse>> response = eventController.getEventsByStore(5L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(eventService).getEventsByStore(5L);
    }

    @Test
    void testGetEventsByService() {
        when(eventService.getEventsByService(10L)).thenReturn(Arrays.asList(responseDto));
        ResponseEntity<List<EventResponse>> response = eventController.getEventsByService(10L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(eventService).getEventsByService(10L);
    }

    @Test
    void testUpdateEvent() {
        when(authentication.getName()).thenReturn("user@esprit.tn");
        when(eventService.updateEvent(eq(1L), any(EventRequest.class), eq("user@esprit.tn"))).thenReturn(responseDto);

        ResponseEntity<EventResponse> response = eventController.updateEvent(1L, request, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        verify(eventService).updateEvent(eq(1L), any(EventRequest.class), eq("user@esprit.tn"));
    }

    @Test
    void testUpdateEventStatus() {
        when(authentication.getName()).thenReturn("admin@esprit.tn");
        when(eventService.updateEventStatus(1L, EventStatus.CANCELLED, "admin@esprit.tn")).thenReturn(responseDto);

        ResponseEntity<EventResponse> response = eventController.updateEventStatus(1L, EventStatus.CANCELLED, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(eventService).updateEventStatus(1L, EventStatus.CANCELLED, "admin@esprit.tn");
    }

    @Test
    void testDeleteEvent() {
        when(authentication.getName()).thenReturn("user@esprit.tn");
        doNothing().when(eventService).deleteEvent(1L, "user@esprit.tn");

        ResponseEntity<Void> response = eventController.deleteEvent(1L, authentication);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(eventService).deleteEvent(1L, "user@esprit.tn");
    }
}
