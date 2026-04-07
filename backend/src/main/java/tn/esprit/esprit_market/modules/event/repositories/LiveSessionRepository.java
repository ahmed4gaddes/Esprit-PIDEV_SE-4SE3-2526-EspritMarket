package tn.esprit.esprit_market.modules.event.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.esprit_market.modules.event.entities.LiveSession;
import tn.esprit.esprit_market.modules.event.enums.LiveSessionStatus;

import java.util.List;

@Repository
public interface LiveSessionRepository extends JpaRepository<LiveSession, Long> {
    List<LiveSession> findByEventId(Long eventId);

    List<LiveSession> findByStoreId(Long storeId);

    List<LiveSession> findByCreatorId(Long creatorId);

    List<LiveSession> findByServiceId(Long serviceId);

    // SCHEDULER : trouver les lives encore actives des events expirés pour les fermer
    List<LiveSession> findByEventIdInAndStatusIn(List<Long> eventIds, List<LiveSessionStatus> statuses);
}
