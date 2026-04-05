package tn.esprit.esprit_market.modules.marketing.service;

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
import tn.esprit.esprit_market.modules.marketing.enums.SponsorshipStatus;
import tn.esprit.esprit_market.modules.marketing.mapper.MarketingMapper;
import tn.esprit.esprit_market.modules.marketing.repository.MarketingCampaignRepository;
import tn.esprit.esprit_market.modules.marketing.repository.SponsorshipRepository;
import tn.esprit.esprit_market.modules.marketing.repository.SponsorshipRequestRepository;
import tn.esprit.esprit_market.modules.marketing.service.impl.SponsorshipServiceImpl;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SponsorshipServiceTest {

    @Mock
    private SponsorshipRepository sponsorshipRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MarketingCampaignRepository marketingCampaignRepository;

    @Mock
    private SponsorshipRequestRepository sponsorshipRequestRepository;

    @Mock
    private MarketingMapper marketingMapper;

    @InjectMocks
    private SponsorshipServiceImpl sponsorshipService;

    private Sponsorship sponsorship;
    private SponsorshipDTO sponsorshipDTO;
    private User sponsor;
    private MarketingCampaign campaign;
    private SponsorshipRequest request;

    @BeforeEach
    void setUp() {
        sponsor = User.builder().id(10L).name("Test Sponsor").build();
        campaign = MarketingCampaign.builder().id(5L).name("Summer Sale").build();
        request = SponsorshipRequest.builder().id(2L).company(User.builder().id(11L).name("TechCorp").build()).build();
        
        sponsorship = Sponsorship.builder().id(1L).amount(1000).status(SponsorshipStatus.ACTIVE)
                .sponsor(sponsor).campaign(campaign).request(request).build();
                
        sponsorshipDTO = SponsorshipDTO.builder().id(1L).amount(1000).status(SponsorshipStatus.ACTIVE)
                .sponsorId(10L).campaignId(5L).requestId(2L).build();
    }

    @Test
    void createSponsorship_ShouldReturnSavedSponsorship() {
        when(userRepository.findById(10L)).thenReturn(Optional.of(sponsor));
        when(marketingCampaignRepository.findById(5L)).thenReturn(Optional.of(campaign));
        when(sponsorshipRequestRepository.findById(2L)).thenReturn(Optional.of(request));
        
        when(marketingMapper.toSponsorshipEntity(any(), any(), any(), any())).thenReturn(sponsorship);
        when(sponsorshipRepository.save(any())).thenReturn(sponsorship);
        when(marketingMapper.toSponsorshipDTO(any())).thenReturn(sponsorshipDTO);

        SponsorshipDTO result = sponsorshipService.createSponsorship(sponsorshipDTO);

        assertNotNull(result);
        assertEquals(1000, result.getAmount());
        assertEquals(SponsorshipStatus.ACTIVE, result.getStatus());
        assertEquals(10L, result.getSponsorId());
        verify(sponsorshipRepository, times(1)).save(any());
    }

    @Test
    void createSponsorship_SponsorNotFound_ShouldThrowException() {
        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> sponsorshipService.createSponsorship(sponsorshipDTO));
        verify(sponsorshipRepository, never()).save(any());
    }

    @Test
    void getSponsorshipById_ShouldReturnSponsorship() {
        when(sponsorshipRepository.findById(1L)).thenReturn(Optional.of(sponsorship));
        when(marketingMapper.toSponsorshipDTO(sponsorship)).thenReturn(sponsorshipDTO);

        SponsorshipDTO result = sponsorshipService.getSponsorshipById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getAllSponsorships_ShouldReturnList() {
        when(sponsorshipRepository.findAll()).thenReturn(List.of(sponsorship));
        when(marketingMapper.toSponsorshipDTO(any())).thenReturn(sponsorshipDTO);

        List<SponsorshipDTO> list = sponsorshipService.getAllSponsorships();

        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
    }

    @Test
    void deleteSponsorship_ShouldDeleteWhenExists() {
        when(sponsorshipRepository.existsById(1L)).thenReturn(true);
        doNothing().when(sponsorshipRepository).deleteById(1L);

        assertDoesNotThrow(() -> sponsorshipService.deleteSponsorship(1L));
        verify(sponsorshipRepository, times(1)).deleteById(1L);
    }
}
