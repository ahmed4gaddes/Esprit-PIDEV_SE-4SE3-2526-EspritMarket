package tn.esprit.esprit_market.modules.marketing.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.marketing.dto.AdvertisementDTO;
import tn.esprit.esprit_market.modules.marketing.entity.Advertisement;
import tn.esprit.esprit_market.modules.marketing.entity.MarketingCampaign;
import tn.esprit.esprit_market.modules.marketing.mapper.MarketingMapper;
import tn.esprit.esprit_market.modules.marketing.repository.AdvertisementRepository;
import tn.esprit.esprit_market.modules.marketing.repository.MarketingCampaignRepository;
import tn.esprit.esprit_market.modules.marketing.service.IAdvertisementService;
import tn.esprit.esprit_market.modules.store.entity.Store;
import tn.esprit.esprit_market.modules.store.repository.StoreRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvertisementServiceImpl implements IAdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final MarketingCampaignRepository marketingCampaignRepository;
    private final StoreRepository storeRepository;
    private final MarketingMapper marketingMapper;

    private MarketingCampaign getCampaign(Long campaignId) {
        if (campaignId == null) return null;
        return marketingCampaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("MarketingCampaign not found: " + campaignId));
    }

    private Set<Store> getStores(Set<Long> storeIds) {
        if (storeIds == null || storeIds.isEmpty()) return new HashSet<>();
        return new HashSet<>(storeRepository.findAllById(storeIds));
    }

    @Override
    @Transactional
    public AdvertisementDTO createAdvertisement(AdvertisementDTO dto) {
        MarketingCampaign campaign = getCampaign(dto.getCampaignId());
        Set<Store> stores = getStores(dto.getStoreIds());

        Advertisement ad = marketingMapper.toAdvertisementEntity(dto, campaign, stores);
        if (ad.getStartDate() == null) ad.setStartDate(new java.util.Date());
        
        Advertisement saved = advertisementRepository.save(ad);
        return marketingMapper.toAdvertisementDTO(saved);
    }

    @Override
    @Transactional
    public AdvertisementDTO updateAdvertisement(Long id, AdvertisementDTO dto) {
        Advertisement existing = advertisementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Advertisement not found: " + id));

        MarketingCampaign campaign = getCampaign(dto.getCampaignId());
        Set<Store> stores = getStores(dto.getStoreIds());

        existing.setTitle(dto.getTitle());
        existing.setContent(dto.getContent());
        existing.setBudget(dto.getBudget());
        existing.setStartDate(dto.getStartDate());
        existing.setEndDate(dto.getEndDate());
        existing.setActive(dto.isActive());
        existing.setCampaign(campaign);
        existing.setStores(stores);

        Advertisement saved = advertisementRepository.save(existing);
        return marketingMapper.toAdvertisementDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AdvertisementDTO getAdvertisementById(Long id) {
        Advertisement ad = advertisementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Advertisement not found: " + id));
        return marketingMapper.toAdvertisementDTO(ad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdvertisementDTO> getAllAdvertisements() {
        return advertisementRepository.findAll().stream()
                .map(marketingMapper::toAdvertisementDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdvertisementDTO> getAdvertisementsByCampaign(Long campaignId) {
        return advertisementRepository.findByCampaign_Id(campaignId).stream()
                .map(marketingMapper::toAdvertisementDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAdvertisement(Long id) {
        if (!advertisementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Advertisement not found: " + id);
        }
        advertisementRepository.deleteById(id);
    }
}
