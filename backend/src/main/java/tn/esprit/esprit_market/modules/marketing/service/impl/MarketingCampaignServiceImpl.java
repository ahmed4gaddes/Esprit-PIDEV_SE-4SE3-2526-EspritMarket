package tn.esprit.esprit_market.modules.marketing.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.marketing.dto.MarketingCampaignDTO;
import tn.esprit.esprit_market.modules.marketing.entity.MarketingCampaign;
import tn.esprit.esprit_market.modules.marketing.mapper.MarketingMapper;
import tn.esprit.esprit_market.modules.marketing.repository.MarketingCampaignRepository;
import tn.esprit.esprit_market.modules.marketing.service.IMarketingCampaignService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarketingCampaignServiceImpl implements IMarketingCampaignService {

    private final MarketingCampaignRepository marketingCampaignRepository;
    private final MarketingMapper marketingMapper;

    @Override
    @Transactional
    public MarketingCampaignDTO createCampaign(MarketingCampaignDTO dto) {
        MarketingCampaign campaign = marketingMapper.toMarketingCampaignEntity(dto);
        MarketingCampaign saved = marketingCampaignRepository.save(campaign);
        return marketingMapper.toMarketingCampaignDTO(saved);
    }

    @Override
    @Transactional
    public MarketingCampaignDTO updateCampaign(Long id, MarketingCampaignDTO dto) {
        MarketingCampaign existing = marketingCampaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MarketingCampaign not found with id: " + id));

        existing.setName(dto.getName());
        existing.setObjective(dto.getObjective());
        existing.setBudget(dto.getBudget());
        existing.setChannel(dto.getChannel());

        MarketingCampaign saved = marketingCampaignRepository.save(existing);
        return marketingMapper.toMarketingCampaignDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public MarketingCampaignDTO getCampaignById(Long id) {
        MarketingCampaign campaign = marketingCampaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MarketingCampaign not found with id: " + id));
        return marketingMapper.toMarketingCampaignDTO(campaign);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarketingCampaignDTO> getAllCampaigns() {
        return marketingCampaignRepository.findAll().stream()
                .map(marketingMapper::toMarketingCampaignDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCampaign(Long id) {
        if (!marketingCampaignRepository.existsById(id)) {
            throw new ResourceNotFoundException("MarketingCampaign not found with id: " + id);
        }
        marketingCampaignRepository.deleteById(id);
    }
}
