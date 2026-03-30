package tn.esprit.esprit_market.modules.event.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tn.esprit.esprit_market.modules.event.dto.TicketRequest;
import tn.esprit.esprit_market.modules.event.dto.TicketResponse;
import tn.esprit.esprit_market.modules.event.service.ITicketService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketControllerTest {

    @Mock private ITicketService ticketService;
    @InjectMocks private TicketController controller;
    private TicketResponse response;

    @BeforeEach
    void setUp() { response = new TicketResponse(); }

    @Test void testCreateTicket() {
        when(ticketService.createTicket(eq(1L), any(TicketRequest.class))).thenReturn(response);
        ResponseEntity<TicketResponse> res = controller.createTicket(1L, new TicketRequest());
        assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test void testGetTicketsByEvent() {
        when(ticketService.getTicketsByEvent(1L)).thenReturn(Arrays.asList(response));
        ResponseEntity<List<TicketResponse>> res = controller.getTicketsByEvent(1L);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testGetTicketsByUser() {
        when(ticketService.getTicketsByUser(1L)).thenReturn(Arrays.asList(response));
        ResponseEntity<List<TicketResponse>> res = controller.getTicketsByUser(1L);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testGetTicketById() {
        when(ticketService.getTicketById(1L)).thenReturn(response);
        ResponseEntity<TicketResponse> res = controller.getTicketById(1L);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testCheckIn() {
        when(ticketService.checkIn(1L)).thenReturn(response);
        ResponseEntity<TicketResponse> res = controller.checkIn(1L);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testDeleteTicket() {
        doNothing().when(ticketService).deleteTicket(1L);
        ResponseEntity<Void> res = controller.deleteTicket(1L);
        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
    }
}
