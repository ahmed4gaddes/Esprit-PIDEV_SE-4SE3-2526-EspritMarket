package tn.esprit.esprit_market.modules.event.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.esprit_market.modules.event.entities.ChatBan;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ChatBanRepository extends JpaRepository<ChatBan, Long> {

    // Find active ban for a user in a specific live session
    @Query("SELECT b FROM ChatBan b WHERE b.user.id = :userId " +
           "AND b.liveSession.id = :liveSessionId " +
           "AND b.bannedUntil > :now " +
           "ORDER BY b.bannedUntil DESC")
    Optional<ChatBan> findActiveBan(@Param("userId") Long userId,
                                    @Param("liveSessionId") Long liveSessionId,
                                    @Param("now") LocalDateTime now);

    // Count messages sent by user in last N seconds (spam detection)
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.sender.id = :userId " +
           "AND m.liveSession.id = :liveSessionId " +
           "AND m.sentAt >= :since")
    long countRecentMessages(@Param("userId") Long userId,
                              @Param("liveSessionId") Long liveSessionId,
                              @Param("since") java.util.Date since);
}
