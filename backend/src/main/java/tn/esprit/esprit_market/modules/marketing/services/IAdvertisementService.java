package tn.esprit.esprit_market.modules.marketing.services;

import tn.esprit.esprit_market.modules.marketing.entity.Advertisement;
import java.util.List;

public interface IAdvertisementService {
    Advertisement add(Advertisement advertisement);
    Advertisement update(Advertisement advertisement);
    Advertisement getById(Long id);
    List<Advertisement> getAll();
    void delete(Long id);

    List<Advertisement> getByCampaign(Long campaignId);
    List<Advertisement> getActiveAdvertisements();
}