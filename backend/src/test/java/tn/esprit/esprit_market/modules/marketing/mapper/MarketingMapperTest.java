package tn.esprit.esprit_market.modules.marketing.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tn.esprit.esprit_market.modules.marketing.dto.*;
import tn.esprit.esprit_market.modules.marketing.entity.*;
import tn.esprit.esprit_market.modules.store.entity.Store;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.marketing.enums.SponsorshipStatus;

import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MarketingMapperTest {

    private MarketingMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new MarketingMapper();
    }

    @Test
    void testSponsorshipRequestMapping() {
        User company = new User();
        company.setId(10L);
        company.setName("Company A");

        User sponsor = new User();
        sponsor.setId(20L);
        sponsor.setName("Sponsor A");

        SponsorshipRequest sr = new SponsorshipRequest();
        sr.setId(1L);
        sr.setOfferTitle("Offer 1");
        sr.setBudget(500.0);
        sr.setCompany(company);
        sr.setSponsor(sponsor);

        SponsorshipRequestDTO dto = mapper.toSponsorshipRequestDTO(sr);
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Offer 1", dto.getOfferTitle());
        assertEquals(500.0, dto.getBudget());
        assertEquals(10L, dto.getCompanyId());
        assertEquals("Company A", dto.getCompanyName());
        assertEquals(20L, dto.getSponsorId());
        assertEquals("Sponsor A", dto.getSponsorName());
    }

    @Test
    void testMarketingCampaignMapping() {
        MarketingCampaign mc = new MarketingCampaign();
        mc.setId(2L);
        mc.setName("Summer Promo");
        mc.setBudget(1000.0);

        MarketingCampaignDTO dto = mapper.toMarketingCampaignDTO(mc);
        assertNotNull(dto);
        assertEquals(2L, dto.getId());
        assertEquals("Summer Promo", dto.getName());
        assertEquals(1000.0, dto.getBudget());

        MarketingCampaign entity = mapper.toMarketingCampaignEntity(dto);
        assertNotNull(entity);
        assertEquals(2L, entity.getId());
        assertEquals("Summer Promo", entity.getName());
        assertEquals(1000.0, entity.getBudget());
    }

    @Test
    void testAdvertisementMapping() {
        MarketingCampaign mc = new MarketingCampaign();
        mc.setId(10L);

        Store s = new Store();
        s.setId(50L);

        Advertisement ad = new Advertisement();
        ad.setId(3L);
        ad.setTitle("Ad 1");
        ad.setCampaign(mc);
        ad.setStores(Set.of(s));

        AdvertisementDTO dto = mapper.toAdvertisementDTO(ad);
        assertNotNull(dto);
        assertEquals(3L, dto.getId());
        assertEquals("Ad 1", dto.getTitle());
        assertEquals(10L, dto.getCampaignId());
        assertTrue(dto.getStoreIds().contains(50L));

        Advertisement entity = mapper.toAdvertisementEntity(dto, mc, Set.of(s));
        assertNotNull(entity);
        assertEquals(3L, entity.getId());
        assertEquals("Ad 1", entity.getTitle());
    }

    @Test
    void testSponsorshipMapping() {
        User sponsor = new User();
        sponsor.setId(10L);

        MarketingCampaign mc = new MarketingCampaign();
        mc.setId(20L);

        SponsorshipRequest sr = new SponsorshipRequest();
        sr.setId(30L);

        Sponsorship sp = new Sponsorship();
        sp.setId(4L);
        sp.setAmount(100.0);
        sp.setStatus(SponsorshipStatus.APPROVED);
        sp.setSponsor(sponsor);
        sp.setCampaign(mc);
        sp.setRequest(sr);

        SponsorshipDTO dto = mapper.toSponsorshipDTO(sp);
        assertNotNull(dto);
        assertEquals(4L, dto.getId());
        assertEquals(100.0, dto.getAmount());
        assertEquals(SponsorshipStatus.APPROVED, dto.getStatus());
        assertEquals(10L, dto.getSponsorId());
        assertEquals(20L, dto.getCampaignId());
        assertEquals(30L, dto.getRequestId());

        Sponsorship entity = mapper.toSponsorshipEntity(dto, sponsor, mc, sr);
        assertNotNull(entity);
        assertEquals(4L, entity.getId());
        assertEquals(100.0, entity.getAmount());
        assertEquals(SponsorshipStatus.APPROVED, entity.getStatus());
    }

    @Test
    void testNulls() {
        assertNull(mapper.toSponsorshipRequestDTO(null));
        assertNull(mapper.toMarketingCampaignDTO(null));
        assertNull(mapper.toMarketingCampaignEntity(null));
        assertNull(mapper.toAdvertisementDTO(null));
        assertNull(mapper.toAdvertisementEntity(null, null, null));
        assertNull(mapper.toSponsorshipDTO(null));
        assertNull(mapper.toSponsorshipEntity(null, null, null, null));
    }
}
