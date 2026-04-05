package tn.esprit.esprit_market.modules.marketing.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.esprit_market.modules.marketing.dto.AdvertisementDTO;
import tn.esprit.esprit_market.modules.marketing.dto.AdvertisementDTO;
import tn.esprit.esprit_market.modules.marketing.dto.MarketingCampaignDTO;
import tn.esprit.esprit_market.modules.marketing.dto.SponsorshipDTO;
import tn.esprit.esprit_market.modules.marketing.dto.SponsorshipRequestDTO;
import tn.esprit.esprit_market.modules.marketing.entity.Advertisement;
import tn.esprit.esprit_market.modules.marketing.entity.MarketingCampaign;
import tn.esprit.esprit_market.modules.marketing.entity.Sponsorship;
import tn.esprit.esprit_market.modules.marketing.entity.SponsorshipRequest;

@Component
public class MarketingMapper {

    public SponsorshipRequestDTO toSponsorshipRequestDTO(SponsorshipRequest request) {
        if (request == null) return null;

        return SponsorshipRequestDTO.builder()
                .id(request.getId())
                .offerTitle(request.getOfferTitle())
                .offerDescription(request.getOfferDescription())
                .budget(request.getBudget())
                .message(request.getMessage())
                .date(request.getDate())
                .state(request.getState())
                .sponsorDesignUrl(request.getSponsorDesignUrl())
                .sponsorNote(request.getSponsorNote())
                .companyId(request.getCompany() != null ? request.getCompany().getId() : null)
                .companyName(request.getCompany() != null ? request.getCompany().getName() : null)
                .sponsorId(request.getSponsor() != null ? request.getSponsor().getId() : null)
                .sponsorName(request.getSponsor() != null ? request.getSponsor().getName() : null)
                .build();
    }

    public MarketingCampaignDTO toMarketingCampaignDTO(MarketingCampaign campaign) {
        if (campaign == null) return null;
        return MarketingCampaignDTO.builder()
                .id(campaign.getId())
                .name(campaign.getName())
                .objective(campaign.getObjective())
                .budget(campaign.getBudget())
                .channel(campaign.getChannel())
                .build();
    }

    public MarketingCampaign toMarketingCampaignEntity(MarketingCampaignDTO dto) {
        if (dto == null) return null;
        return MarketingCampaign.builder()
                .id(dto.getId())
                .name(dto.getName())
                .objective(dto.getObjective())
                .budget(dto.getBudget())
                .channel(dto.getChannel())
                .build();
    }

    public AdvertisementDTO toAdvertisementDTO(Advertisement advertisement) {
        if (advertisement == null) return null;
        return AdvertisementDTO.builder()
                .id(advertisement.getId())
                .title(advertisement.getTitle())
                .content(advertisement.getContent())
                .budget(advertisement.getBudget())
                .startDate(advertisement.getStartDate())
                .endDate(advertisement.getEndDate())
                .active(advertisement.isActive())
                .campaignId(advertisement.getCampaign() != null ? advertisement.getCampaign().getId() : null)
                .storeIds(advertisement.getStores() != null ? 
                    advertisement.getStores().stream().map(store -> store.getId()).collect(java.util.stream.Collectors.toSet()) : 
                    new java.util.HashSet<>())
                .build();
    }

    public Advertisement toAdvertisementEntity(AdvertisementDTO dto, MarketingCampaign campaign, java.util.Set<tn.esprit.esprit_market.modules.store.entity.Store> stores) {
        if (dto == null) return null;
        return Advertisement.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .budget(dto.getBudget())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .active(dto.isActive())
                .campaign(campaign)
                .stores(stores != null ? stores : new java.util.HashSet<>())
                .build();
    }

    public SponsorshipDTO toSponsorshipDTO(Sponsorship sponsorship) {
        if (sponsorship == null) return null;
        return SponsorshipDTO.builder()
                .id(sponsorship.getId())
                .amount(sponsorship.getAmount())
                .status(sponsorship.getStatus())
                .startDate(sponsorship.getStartDate())
                .endDate(sponsorship.getEndDate())
                .sponsorId(sponsorship.getSponsor() != null ? sponsorship.getSponsor().getId() : null)
                .campaignId(sponsorship.getCampaign() != null ? sponsorship.getCampaign().getId() : null)
                .requestId(sponsorship.getRequest() != null ? sponsorship.getRequest().getId() : null)
                .build();
    }

    public Sponsorship toSponsorshipEntity(SponsorshipDTO dto, tn.esprit.esprit_market.modules.user.entity.User sponsor, MarketingCampaign campaign, SponsorshipRequest request) {
        if (dto == null) return null;
        return Sponsorship.builder()
                .id(dto.getId())
                .amount(dto.getAmount())
                .status(dto.getStatus())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .sponsor(sponsor)
                .campaign(campaign)
                .request(request)
                .build();
    }
}
