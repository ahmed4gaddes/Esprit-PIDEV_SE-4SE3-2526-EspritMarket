package tn.esprit.esprit_market.modules.event.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.esprit_market.modules.event.entities.LiveSession;

import java.util.Optional;

@Repository
public interface LiveSessionRepository extends JpaRepository<LiveSession, Long> {
    Optional<LiveSession> findByEventId(Long eventId);
}
