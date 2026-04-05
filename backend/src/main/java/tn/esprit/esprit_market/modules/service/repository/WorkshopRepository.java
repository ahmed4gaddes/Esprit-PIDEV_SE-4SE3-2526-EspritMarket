package tn.esprit.esprit_market.modules.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.esprit_market.modules.service.entity.Workshop;

@Repository
public interface WorkshopRepository extends JpaRepository<Workshop, Long> {
}