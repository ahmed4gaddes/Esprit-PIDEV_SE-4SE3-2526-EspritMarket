package tn.esprit.esprit_market.modules.marketing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.esprit_market.modules.marketing.entity.MarketingCampaign;

@Repository
public interface MarketingCampaignRepository extends JpaRepository<MarketingCampaign, Long> {
}
