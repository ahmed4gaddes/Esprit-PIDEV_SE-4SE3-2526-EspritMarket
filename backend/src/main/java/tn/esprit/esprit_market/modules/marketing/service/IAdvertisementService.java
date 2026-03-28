package tn.esprit.esprit_market.modules.marketing.service;

import tn.esprit.esprit_market.modules.marketing.dto.AdvertisementDTO;

import java.util.List;

public interface IAdvertisementService {
    AdvertisementDTO createAdvertisement(AdvertisementDTO dto);
    AdvertisementDTO updateAdvertisement(Long id, AdvertisementDTO dto);
    AdvertisementDTO getAdvertisementById(Long id);
    List<AdvertisementDTO> getAllAdvertisements();
    List<AdvertisementDTO> getAdvertisementsByCampaign(Long campaignId);
    void deleteAdvertisement(Long id);
}
