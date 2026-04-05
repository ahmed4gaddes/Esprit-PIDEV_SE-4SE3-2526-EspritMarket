package tn.esprit.esprit_market.modules.administration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.esprit_market.modules.administration.entity.Rule;
import tn.esprit.esprit_market.modules.administration.enums.RuleCategory;

import java.util.List;

@Repository
public interface RuleRepository extends JpaRepository<Rule, Long> {
    List<Rule> findByCategory(RuleCategory category);
    List<Rule> findByMandatoryTrue();
}
