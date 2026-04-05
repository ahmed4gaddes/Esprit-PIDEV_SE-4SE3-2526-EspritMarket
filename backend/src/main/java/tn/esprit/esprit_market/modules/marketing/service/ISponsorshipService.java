package tn.esprit.esprit_market.modules.marketing.service;

import tn.esprit.esprit_market.modules.marketing.dto.SponsorshipDTO;

import java.util.List;

public interface ISponsorshipService {
    SponsorshipDTO createSponsorship(SponsorshipDTO dto);
    SponsorshipDTO updateSponsorship(Long id, SponsorshipDTO dto);
    SponsorshipDTO getSponsorshipById(Long id);
    List<SponsorshipDTO> getAllSponsorships();
    List<SponsorshipDTO> getSponsorshipsBySponsor(Long sponsorId);
    List<SponsorshipDTO> getSponsorshipsByCampaign(Long campaignId);
    void deleteSponsorship(Long id);
}
