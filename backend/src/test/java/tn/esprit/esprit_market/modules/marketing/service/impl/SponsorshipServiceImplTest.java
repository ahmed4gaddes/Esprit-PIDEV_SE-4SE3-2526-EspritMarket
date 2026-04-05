package tn.esprit.esprit_market.modules.marketing.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.marketing.dto.SponsorshipDTO;
import tn.esprit.esprit_market.modules.marketing.entity.MarketingCampaign;
import tn.esprit.esprit_market.modules.marketing.entity.Sponsorship;
import tn.esprit.esprit_market.modules.marketing.entity.SponsorshipRequest;
import tn.esprit.esprit_market.modules.marketing.mapper.MarketingMapper;
import tn.esprit.esprit_market.modules.marketing.repository.MarketingCampaignRepository;
import tn.esprit.esprit_market.modules.marketing.repository.SponsorshipRepository;
import tn.esprit.esprit_market.modules.marketing.repository.SponsorshipRequestRepository;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SponsorshipServiceImplTest {

    @Mock private SponsorshipRepository sponsorshipRepository;
    @Mock private UserRepository userRepository;
    @Mock private MarketingCampaignRepository marketingCampaignRepository;
    @Mock private SponsorshipRequestRepository sponsorshipRequestRepository;
    @Mock private MarketingMapper marketingMapper;

    @InjectMocks private SponsorshipServiceImpl sponsorshipService;

    private SponsorshipDTO dto;
    private Sponsorship entity;
    private User sponsor;
    private MarketingCampaign campaign;
    private SponsorshipRequest request;

    @BeforeEach
    void setUp() {
        dto = new SponsorshipDTO();
        dto.setId(1L);
        dto.setSponsorId(2L);
        dto.setCampaignId(3L);
        dto.setRequestId(4L);

        entity = new Sponsorship();
        entity.setId(1L);

        sponsor = new User();
        sponsor.setId(2L);

        campaign = new MarketingCampaign();
        campaign.setId(3L);

        request = new SponsorshipRequest();
        request.setId(4L);
    }

    @Test
    void testCreateSponsorship_Success() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(sponsor));
        when(marketingCampaignRepository.findById(3L)).thenReturn(Optional.of(campaign));
        when(sponsorshipRequestRepository.findById(4L)).thenReturn(Optional.of(request));
        
        when(marketingMapper.toSponsorshipEntity(dto, sponsor, campaign, request)).thenReturn(entity);
        when(sponsorshipRepository.save(entity)).thenReturn(entity);
        when(marketingMapper.toSponsorshipDTO(entity)).thenReturn(dto);

        SponsorshipDTO res = sponsorshipService.createSponsorship(dto);
        assertNotNull(res);
        assertEquals(1L, res.getId());
    }

    @Test
    void testCreateSponsorship_NullIds() {
        SponsorshipDTO emptyDto = new SponsorshipDTO();
        when(marketingMapper.toSponsorshipEntity(emptyDto, null, null, null)).thenReturn(entity);
        when(sponsorshipRepository.save(entity)).thenReturn(entity);
        when(marketingMapper.toSponsorshipDTO(entity)).thenReturn(emptyDto);

        SponsorshipDTO res = sponsorshipService.createSponsorship(emptyDto);
        assertNotNull(res);
    }

    @Test
    void testCreateSponsorship_SponsorNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> sponsorshipService.createSponsorship(dto));
    }

    @Test
    void testUpdateSponsorship_Success() {
        when(sponsorshipRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(userRepository.findById(2L)).thenReturn(Optional.of(sponsor));
        when(marketingCampaignRepository.findById(3L)).thenReturn(Optional.of(campaign));
        when(sponsorshipRequestRepository.findById(4L)).thenReturn(Optional.of(request));
        
        when(sponsorshipRepository.save(entity)).thenReturn(entity);
        when(marketingMapper.toSponsorshipDTO(entity)).thenReturn(dto);

        SponsorshipDTO res = sponsorshipService.updateSponsorship(1L, dto);
        assertNotNull(res);
    }

    @Test
    void testUpdateSponsorship_NullIds() {
        when(sponsorshipRepository.findById(1L)).thenReturn(Optional.of(entity));
        SponsorshipDTO emptyDto = new SponsorshipDTO();
        when(sponsorshipRepository.save(entity)).thenReturn(entity);
        when(marketingMapper.toSponsorshipDTO(entity)).thenReturn(emptyDto);

        SponsorshipDTO res = sponsorshipService.updateSponsorship(1L, emptyDto);
        assertNotNull(res);
    }

    @Test
    void testGetSponsorshipById() {
        when(sponsorshipRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(marketingMapper.toSponsorshipDTO(entity)).thenReturn(dto);

        assertNotNull(sponsorshipService.getSponsorshipById(1L));
    }

    @Test
    void testGetAllSponsorships() {
        when(sponsorshipRepository.findAll()).thenReturn(Arrays.asList(entity));
        when(marketingMapper.toSponsorshipDTO(entity)).thenReturn(dto);

        assertEquals(1, sponsorshipService.getAllSponsorships().size());
    }

    @Test
    void testGetSponsorshipsBySponsor() {
        when(sponsorshipRepository.findBySponsor_Id(2L)).thenReturn(Arrays.asList(entity));
        when(marketingMapper.toSponsorshipDTO(entity)).thenReturn(dto);

        assertEquals(1, sponsorshipService.getSponsorshipsBySponsor(2L).size());
    }

    @Test
    void testGetSponsorshipsByCampaign() {
        when(sponsorshipRepository.findByCampaign_Id(3L)).thenReturn(Arrays.asList(entity));
        when(marketingMapper.toSponsorshipDTO(entity)).thenReturn(dto);

        assertEquals(1, sponsorshipService.getSponsorshipsByCampaign(3L).size());
    }

    @Test
    void testDeleteSponsorship() {
        when(sponsorshipRepository.existsById(1L)).thenReturn(true);
        doNothing().when(sponsorshipRepository).deleteById(1L);

        assertDoesNotThrow(() -> sponsorshipService.deleteSponsorship(1L));
    }

    @Test
    void testDeleteSponsorship_NotFound() {
        when(sponsorshipRepository.existsById(1L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> sponsorshipService.deleteSponsorship(1L));
    }
}
