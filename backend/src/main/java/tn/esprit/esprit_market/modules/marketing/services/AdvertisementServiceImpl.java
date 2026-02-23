package tn.esprit.esprit_market.modules.marketing.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.modules.marketing.entity.Advertisement;
import tn.esprit.esprit_market.modules.marketing.entity.MarketingCampaign;
import tn.esprit.esprit_market.modules.marketing.repository.AdvertisementRepository;
import tn.esprit.esprit_market.modules.marketing.repository.MarketingCampaignRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdvertisementServiceImpl implements IAdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final MarketingCampaignRepository campaignRepository;

    @Override
    @Transactional
    public Advertisement add(Advertisement advertisement) {
        if (advertisement.getCampaign() != null && advertisement.getCampaign().getId() != null) {
            MarketingCampaign campaign = campaignRepository.findById(advertisement.getCampaign().getId())
                    .orElseThrow(() -> new RuntimeException("Campagne non trouvée"));
            advertisement.setCampaign(campaign);
        }

        return advertisementRepository.save(advertisement);
    }

    @Override
    @Transactional
    public Advertisement update(Advertisement advertisement) {
        return advertisementRepository.save(advertisement);
    }

    @Override
    public Advertisement getById(Long id) {
        return advertisementRepository.findById(id).orElse(null);
    }

    @Override
    public List<Advertisement> getAll() {
        return advertisementRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        advertisementRepository.deleteById(id);
    }

    @Override
    public List<Advertisement> getByCampaign(Long campaignId) {
        return List.of();
    }

    @Override
    public List<Advertisement> getActiveAdvertisements() {
        return List.of();
    }
}