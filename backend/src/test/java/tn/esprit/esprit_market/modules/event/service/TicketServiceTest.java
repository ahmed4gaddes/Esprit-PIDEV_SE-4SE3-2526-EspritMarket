package tn.esprit.esprit_market.modules.event.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.event.dto.TicketRequest;
import tn.esprit.esprit_market.modules.event.dto.TicketResponse;
import tn.esprit.esprit_market.modules.event.entities.Event;
import tn.esprit.esprit_market.modules.event.entities.Ticket;
import tn.esprit.esprit_market.modules.event.repositories.EventRepository;
import tn.esprit.esprit_market.modules.event.repositories.TicketRepository;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.service.IUserService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private IUserService userService;

    @InjectMocks
    private TicketService ticketService;

    private Event fakeEvent;
    private User fakeUser;
    private Ticket fakeTicket;
    private TicketRequest fakeRequest;

    @BeforeEach
    void setUp() {
        fakeUser = new User();
        fakeUser.setId(1L);

        fakeEvent = Event.builder()
                .id(1L)
                .title("Concert")
                .capacity(100)
                .build();

        fakeTicket = Ticket.builder()
                .id(1L)
                .price(50.0)
                .qrCode("uuid-1234")
                .checkedIn(false)
                .event(fakeEvent)
                .user(fakeUser)
                .build();

        fakeRequest = TicketRequest.builder()
                .userId(1L)
                .price(50.0)
                .build();
    }

    // ==================== CREATE ====================
    @Test
    void testCreateTicket_Success() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(fakeEvent));
        when(userService.getUserById(1L)).thenReturn(fakeUser);
        when(ticketRepository.findByEventId(1L)).thenReturn(Arrays.asList()); // 0 tickets sold
        when(ticketRepository.save(any(Ticket.class))).thenReturn(fakeTicket);

        TicketResponse result = ticketService.createTicket(1L, fakeRequest);

        assertNotNull(result);
        assertEquals(50.0, result.getPrice());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void testCreateTicket_EventFull_ThrowsException() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(fakeEvent));
        when(userService.getUserById(1L)).thenReturn(fakeUser);
        
        // Mock 100 tickets already sold
        Ticket[] mockTickets = new Ticket[100];
        when(ticketRepository.findByEventId(1L)).thenReturn(Arrays.asList(mockTickets));

        assertThrows(IllegalStateException.class, () -> ticketService.createTicket(1L, fakeRequest));
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void testCreateTicket_InvalidEvent_ThrowsException() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ticketService.createTicket(99L, fakeRequest));
        verify(ticketRepository, never()).save(any());
    }

    // ==================== READ ====================
    @Test
    void testGetTicketsByEvent_ReturnsList() {
        when(ticketRepository.findByEventId(1L)).thenReturn(Arrays.asList(fakeTicket));

        List<TicketResponse> result = ticketService.getTicketsByEvent(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetTicketById_Found() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(fakeTicket));

        TicketResponse result = ticketService.getTicketById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    // ==================== CHECK-IN ====================
    @Test
    void testCheckIn_Success() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(fakeTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(fakeTicket);

        TicketResponse result = ticketService.checkIn(1L);

        assertNotNull(result);
        assertTrue(fakeTicket.isCheckedIn());
        verify(ticketRepository, times(1)).save(fakeTicket);
    }

    @Test
    void testCheckIn_AlreadyCheckedIn_ThrowsException() {
        fakeTicket.setCheckedIn(true);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(fakeTicket));

        assertThrows(IllegalStateException.class, () -> ticketService.checkIn(1L));
    }

    // ==================== DELETE ====================
    @Test
    void testDeleteTicket_Success() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(fakeTicket));
        doNothing().when(ticketRepository).delete(fakeTicket);

        ticketService.deleteTicket(1L);

        verify(ticketRepository, times(1)).delete(fakeTicket);
    }
}
