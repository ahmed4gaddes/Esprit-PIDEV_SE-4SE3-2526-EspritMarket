package tn.esprit.esprit_market.modules.marketing.services;

import tn.esprit.esprit_market.modules.marketing.entity.MarketingCampaign;
import java.util.List;

public interface IMarketingCampaignService {
    MarketingCampaign add(MarketingCampaign campaign);
    MarketingCampaign update(MarketingCampaign campaign);
    MarketingCampaign getById(Long id);
    List<MarketingCampaign> getAll();
    void delete(Long id);

    List<MarketingCampaign> getByChannel(String channel);
}