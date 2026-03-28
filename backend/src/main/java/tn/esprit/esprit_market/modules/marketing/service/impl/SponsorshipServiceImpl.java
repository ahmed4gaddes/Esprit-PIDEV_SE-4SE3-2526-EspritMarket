package tn.esprit.esprit_market.modules.marketing.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.marketing.dto.SponsorshipDTO;
import tn.esprit.esprit_market.modules.marketing.entity.MarketingCampaign;
import tn.esprit.esprit_market.modules.marketing.entity.Sponsorship;
import tn.esprit.esprit_market.modules.marketing.entity.SponsorshipRequest;
import tn.esprit.esprit_market.modules.marketing.mapper.MarketingMapper;
import tn.esprit.esprit_market.modules.marketing.repository.MarketingCampaignRepository;
import tn.esprit.esprit_market.modules.marketing.repository.SponsorshipRepository;
import tn.esprit.esprit_market.modules.marketing.repository.SponsorshipRequestRepository;
import tn.esprit.esprit_market.modules.marketing.service.ISponsorshipService;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SponsorshipServiceImpl implements ISponsorshipService {

    private final SponsorshipRepository sponsorshipRepository;
    private final UserRepository userRepository;
    private final MarketingCampaignRepository marketingCampaignRepository;
    private final SponsorshipRequestRepository sponsorshipRequestRepository;
    private final MarketingMapper marketingMapper;

    @Override
    @Transactional
    public SponsorshipDTO createSponsorship(SponsorshipDTO dto) {
        User sponsor = null;
        if (dto.getSponsorId() != null) {
            sponsor = userRepository.findById(dto.getSponsorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sponsor not found: " + dto.getSponsorId()));
        }

        MarketingCampaign campaign = null;
        if (dto.getCampaignId() != null) {
            campaign = marketingCampaignRepository.findById(dto.getCampaignId())
                    .orElseThrow(() -> new ResourceNotFoundException("Campaign not found: " + dto.getCampaignId()));
        }

        SponsorshipRequest request = null;
        if (dto.getRequestId() != null) {
            request = sponsorshipRequestRepository.findById(dto.getRequestId())
                    .orElseThrow(() -> new ResourceNotFoundException("Request not found: " + dto.getRequestId()));
        }

        Sponsorship sponsorship = marketingMapper.toSponsorshipEntity(dto, sponsor, campaign, request);
        return marketingMapper.toSponsorshipDTO(sponsorshipRepository.save(sponsorship));
    }

    @Override
    @Transactional
    public SponsorshipDTO updateSponsorship(Long id, SponsorshipDTO dto) {
        Sponsorship existing = sponsorshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sponsorship not found: " + id));

        if (dto.getSponsorId() != null) {
            existing.setSponsor(userRepository.findById(dto.getSponsorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sponsor not found: " + dto.getSponsorId())));
        }
        if (dto.getCampaignId() != null) {
            existing.setCampaign(marketingCampaignRepository.findById(dto.getCampaignId())
                    .orElseThrow(() -> new ResourceNotFoundException("Campaign not found: " + dto.getCampaignId())));
        }
        if (dto.getRequestId() != null) {
            existing.setRequest(sponsorshipRequestRepository.findById(dto.getRequestId())
                    .orElseThrow(() -> new ResourceNotFoundException("Request not found: " + dto.getRequestId())));
        }

        existing.setAmount(dto.getAmount());
        existing.setStatus(dto.getStatus());
        existing.setStartDate(dto.getStartDate());
        existing.setEndDate(dto.getEndDate());

        return marketingMapper.toSponsorshipDTO(sponsorshipRepository.save(existing));
    }

    @Override
    @Transactional(readOnly = true)
    public SponsorshipDTO getSponsorshipById(Long id) {
        return marketingMapper.toSponsorshipDTO(sponsorshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sponsorship not found: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SponsorshipDTO> getAllSponsorships() {
        return sponsorshipRepository.findAll().stream()
                .map(marketingMapper::toSponsorshipDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SponsorshipDTO> getSponsorshipsBySponsor(Long sponsorId) {
        return sponsorshipRepository.findBySponsor_Id(sponsorId).stream()
                .map(marketingMapper::toSponsorshipDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SponsorshipDTO> getSponsorshipsByCampaign(Long campaignId) {
        return sponsorshipRepository.findByCampaign_Id(campaignId).stream()
                .map(marketingMapper::toSponsorshipDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteSponsorship(Long id) {
        if (!sponsorshipRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sponsorship not found: " + id);
        }
        sponsorshipRepository.deleteById(id);
    }
}
