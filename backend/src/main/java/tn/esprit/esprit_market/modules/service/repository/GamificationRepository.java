package tn.esprit.esprit_market.modules.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.esprit_market.modules.service.entity.Gamification;

@Repository
public interface GamificationRepository extends JpaRepository<Gamification, Long> {
}