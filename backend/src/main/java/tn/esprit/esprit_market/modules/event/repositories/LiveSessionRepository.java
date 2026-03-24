package tn.esprit.esprit_market.modules.event.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.esprit_market.modules.event.entities.LiveSession;

import java.util.List;
import java.util.Optional;

@Repository
public interface LiveSessionRepository extends JpaRepository<LiveSession, Long> {
    List<LiveSession> findByEventId(Long eventId);

    List<LiveSession> findByStoreId(Long storeId);

    List<LiveSession> findByCreatorId(Long creatorId);
}
