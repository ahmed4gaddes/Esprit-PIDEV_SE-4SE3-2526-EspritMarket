package tn.esprit.esprit_market.modules.marketing.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.modules.marketing.entity.MarketingCampaign;
import tn.esprit.esprit_market.modules.marketing.repository.MarketingCampaignRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketingCampaignServiceImpl implements IMarketingCampaignService {

    private final MarketingCampaignRepository campaignRepository;

    @Override
    public MarketingCampaign add(MarketingCampaign campaign) {
        return campaignRepository.save(campaign);
    }

    @Override
    public MarketingCampaign update(MarketingCampaign campaign) {
        return campaignRepository.save(campaign);
    }

    @Override
    public MarketingCampaign getById(Long id) {
        return campaignRepository.findById(id).orElse(null);
    }

    @Override
    public List<MarketingCampaign> getAll() {
        return campaignRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        campaignRepository.deleteById(id);
    }

    @Override
    public List<MarketingCampaign> getByChannel(String channel) {
        return List.of();
    }
}