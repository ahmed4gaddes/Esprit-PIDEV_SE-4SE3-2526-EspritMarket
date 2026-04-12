package tn.esprit.esprit_market.modules.event.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.esprit_market.modules.event.dto.EventStatisticsDTO;
import tn.esprit.esprit_market.modules.event.entities.Event;
import tn.esprit.esprit_market.modules.event.enums.EventStatus;
import tn.esprit.esprit_market.modules.event.enums.EventType;
import tn.esprit.esprit_market.modules.user.enums.Role;

import java.util.Date;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByType(EventType type);

    List<Event> findByStoreId(Long storeId);

    List<Event> findByServiceId(Long serviceId);

    // =====================================================================
    // SCHEDULER : trouver les events expirés à archiver
    // =====================================================================
    List<Event> findByDateBeforeAndStatusIn(Date date, List<EventStatus> statuses);

    // =====================================================================
    // JPQL avec JOIN : statistiques par organisateur (3 tables : Event + User + Ticket)
    // =====================================================================
    @Query("SELECT new tn.esprit.esprit_market.modules.event.dto.EventStatisticsDTO(" +
           "e.id, e.title, e.date, e.status, u.name, " +
           "COUNT(t), COALESCE(SUM(t.price), 0.0)) " +
           "FROM Event e " +
           "JOIN e.organizer u " +
           "LEFT JOIN e.tickets t " +
           "WHERE u.id = :organizerId " +
           "GROUP BY e.id, e.title, e.date, e.status, u.name " +
           "ORDER BY e.date DESC")
    List<EventStatisticsDTO> findEventStatisticsByOrganizer(@Param("organizerId") Long organizerId);

    // =====================================================================
    // NOUVEAUX KEYWORDS : Recherche pour les événements d'un Seller
    // =====================================================================

    // Keyword 1 : Chercher les événements d'un organisateur (Seller) par le nom (Titre de l'Event)
    List<Event> findByOrganizerIdAndTitleContainingIgnoreCase(Long organizerId, String title);

    // Keyword 2 : Chercher les événements d'un organisateur (Seller) créés après une certaine date
    List<Event> findByOrganizerIdAndCreatedAtAfter(Long organizerId, Date date);
}
