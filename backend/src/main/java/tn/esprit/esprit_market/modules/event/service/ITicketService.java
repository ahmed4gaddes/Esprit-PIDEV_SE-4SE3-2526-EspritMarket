package tn.esprit.esprit_market.modules.event.service;

import tn.esprit.esprit_market.modules.event.dto.TicketRequest;
import tn.esprit.esprit_market.modules.event.dto.TicketResponse;

import java.util.List;

public interface ITicketService {
    TicketResponse createTicket(Long eventId, TicketRequest request);
    List<TicketResponse> getTicketsByEvent(Long eventId);
    List<TicketResponse> getTicketsByUser(Long userId);
    TicketResponse getTicketById(Long id);
    TicketResponse checkIn(Long id);
    void deleteTicket(Long id);
}
