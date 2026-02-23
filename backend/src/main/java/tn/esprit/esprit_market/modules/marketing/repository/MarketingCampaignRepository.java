package tn.esprit.esprit_market.modules.marketing.repository;

import tn.esprit.esprit_market.modules.marketing.entity.MarketingCampaign;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketingCampaignRepository extends JpaRepository<MarketingCampaign, Long> {
}