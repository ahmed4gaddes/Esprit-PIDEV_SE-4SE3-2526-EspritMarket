package tn.esprit.esprit_market.modules.event.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.esprit_market.modules.event.entities.Ticket;
import tn.esprit.esprit_market.modules.event.enums.TicketStatus;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByEventId(Long eventId);

    List<Ticket> findByUserId(Long userId);

    // SCHEDULER : trouver les tickets VALID des events expirés pour les passer à EXPIRED
    List<Ticket> findByEventIdInAndStatus(List<Long> eventIds, TicketStatus status);
}
