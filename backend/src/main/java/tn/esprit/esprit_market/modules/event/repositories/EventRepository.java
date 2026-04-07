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
    // KEYWORDS multi-table : navigation de propriétés (Event → User, Event → Store)
    // =====================================================================

    // Keyword 1 : Chercher par rôle de l'organisateur ET statut de l'event
    // Traverse Event → User (organizer) → role = 2 tables (events + users)
    List<Event> findByOrganizerRoleAndStatus(Role role, EventStatus status);

    // Keyword 2 : Chercher par nom du store (contient) ET type d'event
    // Traverse Event → Store → name = 2 tables (events + stores)
    List<Event> findByStoreNameContainingIgnoreCaseAndType(String storeName, EventType type);

    // Keyword 3 : Events à venir d'un organisateur avec date après une date donnée
    // Traverse Event → User (organizer) → id = 2 tables (events + users)
    List<Event> findByOrganizerIdAndStatusAndDateAfter(Long organizerId, EventStatus status, Date date);
}
