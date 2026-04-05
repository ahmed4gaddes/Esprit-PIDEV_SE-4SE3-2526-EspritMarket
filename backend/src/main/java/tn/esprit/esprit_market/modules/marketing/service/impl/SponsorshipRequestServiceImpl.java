package tn.esprit.esprit_market.modules.marketing.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.marketing.dto.SponsorshipRequestDTO;
import tn.esprit.esprit_market.modules.marketing.entity.SponsorshipRequest;
import tn.esprit.esprit_market.modules.marketing.mapper.MarketingMapper;
import tn.esprit.esprit_market.modules.marketing.repository.SponsorshipRequestRepository;
import tn.esprit.esprit_market.modules.marketing.service.ISponsorshipRequestService;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SponsorshipRequestServiceImpl implements ISponsorshipRequestService {

    private final SponsorshipRequestRepository sponsorshipRequestRepository;
    private final UserRepository userRepository;
    private final MarketingMapper marketingMapper;

    @Override
    @Transactional
    public SponsorshipRequestDTO createOffer(Long companyId, SponsorshipRequest input) {
        User company = userRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));

        SponsorshipRequest request = SponsorshipRequest.builder()
                .offerTitle(input.getOfferTitle())
                .offerDescription(input.getOfferDescription())
                .budget(input.getBudget())
                .message(input.getMessage())
                .date(new Date())
                .state("PENDING")
                .company(company)
                .build();
                
        SponsorshipRequest saved = sponsorshipRequestRepository.save(request);
        return marketingMapper.toSponsorshipRequestDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SponsorshipRequestDTO> getCompanyOffers(Long companyId) {
        return sponsorshipRequestRepository.findByCompany_IdOrderByDateDesc(companyId)
                .stream().map(marketingMapper::toSponsorshipRequestDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SponsorshipRequestDTO> getSponsorInbox() {
        return sponsorshipRequestRepository.findByStateOrderByDateDesc("PENDING")
                .stream().map(marketingMapper::toSponsorshipRequestDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SponsorshipRequestDTO> getSponsorHistory(Long sponsorId) {
        return sponsorshipRequestRepository.findBySponsor_IdOrderByDateDesc(sponsorId)
                .stream().map(marketingMapper::toSponsorshipRequestDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SponsorshipRequestDTO decide(Long requestId, Long sponsorId, boolean approved, String designUrl, String note) {
        SponsorshipRequest request = sponsorshipRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found with id: " + requestId));

        User sponsor = userRepository.findById(sponsorId)
                .orElseThrow(() -> new ResourceNotFoundException("Sponsor not found with id: " + sponsorId));

        request.setSponsor(sponsor);
        request.setState(approved ? "APPROVED" : "REJECTED");
        request.setSponsorNote(note);
        if (approved) {
            request.setSponsorDesignUrl(designUrl);
        } else {
            request.setSponsorDesignUrl(null);
        }

        SponsorshipRequest saved = sponsorshipRequestRepository.save(request);
        return marketingMapper.toSponsorshipRequestDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SponsorshipRequestDTO> getApprovedAdsForCustomers() {
        return sponsorshipRequestRepository.findByStateAndSponsorDesignUrlIsNotNullOrderByDateDesc("APPROVED")
                .stream().map(marketingMapper::toSponsorshipRequestDTO)
                .collect(Collectors.toList());
    }
}
