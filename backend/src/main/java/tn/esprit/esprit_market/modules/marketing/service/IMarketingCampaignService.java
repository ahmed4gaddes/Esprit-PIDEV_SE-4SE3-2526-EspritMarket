package tn.esprit.esprit_market.modules.marketing.service;

import tn.esprit.esprit_market.modules.marketing.dto.MarketingCampaignDTO;
import java.util.List;

public interface IMarketingCampaignService {
    MarketingCampaignDTO createCampaign(MarketingCampaignDTO dto);
    MarketingCampaignDTO updateCampaign(Long id, MarketingCampaignDTO dto);
    MarketingCampaignDTO getCampaignById(Long id);
    List<MarketingCampaignDTO> getAllCampaigns();
    void deleteCampaign(Long id);
}
