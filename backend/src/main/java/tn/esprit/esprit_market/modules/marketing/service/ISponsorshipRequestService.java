package tn.esprit.esprit_market.modules.marketing.service;

import tn.esprit.esprit_market.modules.marketing.dto.SponsorshipRequestDTO;
import tn.esprit.esprit_market.modules.marketing.entity.SponsorshipRequest;

import java.util.List;

public interface ISponsorshipRequestService {
    SponsorshipRequestDTO createOffer(Long companyId, SponsorshipRequest input);
    List<SponsorshipRequestDTO> getCompanyOffers(Long companyId);
    List<SponsorshipRequestDTO> getSponsorInbox();
    List<SponsorshipRequestDTO> getSponsorHistory(Long sponsorId);
    SponsorshipRequestDTO decide(Long requestId, Long sponsorId, boolean approved, String designUrl, String note);
    List<SponsorshipRequestDTO> getApprovedAdsForCustomers();
}
