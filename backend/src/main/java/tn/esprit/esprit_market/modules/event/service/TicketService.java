package tn.esprit.esprit_market.modules.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.event.dto.DynamicPriceResponse;
import tn.esprit.esprit_market.modules.event.dto.TicketRequest;
import tn.esprit.esprit_market.modules.event.dto.TicketResponse;
import tn.esprit.esprit_market.modules.event.entities.Event;
import tn.esprit.esprit_market.modules.event.entities.Ticket;
import tn.esprit.esprit_market.modules.event.repositories.EventRepository;
import tn.esprit.esprit_market.modules.event.repositories.TicketRepository;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.service.IUserService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService implements ITicketService {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final IUserService userService;
    private final DynamicPricingService dynamicPricingService;

    // ==================== CREATE ====================
    public TicketResponse createTicket(Long eventId, TicketRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));

        User user = userService.getUserById(request.getUserId());

        // Vérifier la capacité
        long currentTickets = ticketRepository.findByEventId(eventId).size();
        if (currentTickets >= event.getCapacity()) {
            throw new IllegalStateException("Event is full. Capacity: " + event.getCapacity());
        }

        // Calculate the dynamic price (or use base price if no dynamic pricing)
        DynamicPriceResponse priceInfo = dynamicPricingService.calculateCurrentPrice(eventId);
        double ticketPrice = priceInfo.getCurrentPrice();

        Ticket ticket = Ticket.builder()
                .price(ticketPrice)
                .qrCode(UUID.randomUUID().toString()) // Génération QR code (UUID)
                .checkedIn(false)
                .event(event)
                .user(user)
                .build();

        Ticket savedTicket = ticketRepository.save(ticket);
        return mapToResponse(savedTicket);
    }

    // ==================== READ BY EVENT ====================
    public List<TicketResponse> getTicketsByEvent(Long eventId) {
        return ticketRepository.findByEventId(eventId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ==================== READ BY USER ====================
    public List<TicketResponse> getTicketsByUser(Long userId) {
        return ticketRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ==================== READ ONE ====================
    public TicketResponse getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
        return mapToResponse(ticket);
    }

    // ==================== CHECK-IN ====================
    public TicketResponse checkIn(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));

        if (ticket.isCheckedIn()) {
            throw new IllegalStateException("Ticket already checked in");
        }

        ticket.setCheckedIn(true);
        Ticket updatedTicket = ticketRepository.save(ticket);
        return mapToResponse(updatedTicket);
    }

    // ==================== DELETE ====================
    public void deleteTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
        ticketRepository.delete(ticket);
    }

    // ==================== MAPPER ====================
    private TicketResponse mapToResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .price(ticket.getPrice())
                .qrCode(ticket.getQrCode())
                .checkedIn(ticket.isCheckedIn())
                .status(ticket.getStatus())
                .seatNumber(ticket.getSeatNumber())
                .purchaseDate(ticket.getPurchaseDate())
                .eventId(ticket.getEvent() != null ? ticket.getEvent().getId() : null)
                .eventTitle(ticket.getEvent() != null ? ticket.getEvent().getTitle() : null)
                .userId(ticket.getUser() != null ? ticket.getUser().getId() : null)
                .userName(ticket.getUser() != null ? ticket.getUser().getName() : null)
                .build();
    }
}
