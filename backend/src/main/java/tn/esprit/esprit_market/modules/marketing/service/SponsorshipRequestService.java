package tn.esprit.esprit_market.modules.marketing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.modules.marketing.entity.SponsorshipRequest;
import tn.esprit.esprit_market.modules.marketing.repository.SponsorshipRequestRepository;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SponsorshipRequestService {

    private final SponsorshipRequestRepository sponsorshipRequestRepository;
    private final UserRepository userRepository;

    public SponsorshipRequest createOffer(Long companyId, SponsorshipRequest input) {
        User company = userRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + companyId));

        SponsorshipRequest request = SponsorshipRequest.builder()
                .offerTitle(input.getOfferTitle())
                .offerDescription(input.getOfferDescription())
                .budget(input.getBudget())
                .message(input.getMessage())
                .date(new Date())
                .state("PENDING")
                .company(company)
                .build();
        return sponsorshipRequestRepository.save(request);
    }

    public List<SponsorshipRequest> getCompanyOffers(Long companyId) {
        return sponsorshipRequestRepository.findByCompany_IdOrderByDateDesc(companyId);
    }

    public List<SponsorshipRequest> getSponsorInbox() {
        return sponsorshipRequestRepository.findByStateOrderByDateDesc("PENDING");
    }

    public List<SponsorshipRequest> getSponsorHistory(Long sponsorId) {
        return sponsorshipRequestRepository.findBySponsor_IdOrderByDateDesc(sponsorId);
    }

    public SponsorshipRequest decide(Long requestId, Long sponsorId, boolean approved, String designUrl, String note) {
        SponsorshipRequest request = sponsorshipRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Offer not found with id: " + requestId));

        User sponsor = userRepository.findById(sponsorId)
                .orElseThrow(() -> new RuntimeException("Sponsor not found with id: " + sponsorId));

        request.setSponsor(sponsor);
        request.setState(approved ? "APPROVED" : "REJECTED");
        request.setSponsorNote(note);
        if (approved) {
            request.setSponsorDesignUrl(designUrl);
        } else {
            request.setSponsorDesignUrl(null);
        }

        return sponsorshipRequestRepository.save(request);
    }

    public List<SponsorshipRequest> getApprovedAdsForCustomers() {
        return sponsorshipRequestRepository.findByStateAndSponsorDesignUrlIsNotNullOrderByDateDesc("APPROVED");
    }
}

