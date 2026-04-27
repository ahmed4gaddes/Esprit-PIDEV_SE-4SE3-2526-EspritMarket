package tn.esprit.esprit_market.modules.event.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.esprit_market.modules.event.entities.PricingRule;

import java.util.Optional;

@Repository
public interface PricingRuleRepository extends JpaRepository<PricingRule, Long> {

    // Spring Data Keyword: find the pricing rule for a specific event
    Optional<PricingRule> findByEventId(Long eventId);

    // Spring Data Keyword: check if an event already has a pricing rule
    boolean existsByEventId(Long eventId);

    // Spring Data Keyword: delete pricing rule by event id
    void deleteByEventId(Long eventId);
}
