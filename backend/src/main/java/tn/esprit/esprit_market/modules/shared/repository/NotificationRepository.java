package tn.esprit.esprit_market.modules.shared.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.esprit_market.modules.shared.entity.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser_IdOrderByCreatedAtDesc(Long userId);
}
