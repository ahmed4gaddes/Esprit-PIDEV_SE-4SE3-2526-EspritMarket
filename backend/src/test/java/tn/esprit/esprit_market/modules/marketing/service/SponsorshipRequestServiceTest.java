package tn.esprit.esprit_market.modules.marketing.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.marketing.dto.SponsorshipRequestDTO;
import tn.esprit.esprit_market.modules.marketing.entity.SponsorshipRequest;
import tn.esprit.esprit_market.modules.marketing.mapper.MarketingMapper;
import tn.esprit.esprit_market.modules.marketing.repository.SponsorshipRequestRepository;
import tn.esprit.esprit_market.modules.marketing.service.impl.SponsorshipRequestServiceImpl;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SponsorshipRequestServiceTest {

    @Mock
    private SponsorshipRequestRepository sponsorshipRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MarketingMapper marketingMapper;

    @InjectMocks
    private SponsorshipRequestServiceImpl service;

    private SponsorshipRequest request;
    private SponsorshipRequestDTO requestDTO;
    private User company;
    private User sponsor;

    @BeforeEach
    void setUp() {
        company = User.builder().id(11L).name("TechCorp").build();
        sponsor = User.builder().id(10L).name("Test Sponsor").build();

        request = SponsorshipRequest.builder().id(1L).offerTitle("Title").offerDescription("Desc")
                .budget(500.0).message("Message").date(new Date()).state("PENDING").company(company).build();

        requestDTO = SponsorshipRequestDTO.builder().id(1L).offerTitle("Title").state("PENDING")
                .companyId(11L).companyName("TechCorp").build();
    }

    @Test
    void createOffer_ShouldReturnSavedRequest() {
        when(userRepository.findById(11L)).thenReturn(Optional.of(company));
        when(sponsorshipRequestRepository.save(any(SponsorshipRequest.class))).thenReturn(request);
        when(marketingMapper.toSponsorshipRequestDTO(any(SponsorshipRequest.class))).thenReturn(requestDTO);

        SponsorshipRequest input = new SponsorshipRequest();
        input.setOfferTitle("Title");

        SponsorshipRequestDTO result = service.createOffer(11L, input);

        assertNotNull(result);
        assertEquals("Title", result.getOfferTitle());
        assertEquals(11L, result.getCompanyId());
        verify(sponsorshipRequestRepository, times(1)).save(any(SponsorshipRequest.class));
    }

    @Test
    void createOffer_CompanyNotFound_ShouldThrowException() {
        when(userRepository.findById(11L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.createOffer(11L, new SponsorshipRequest()));
        verify(sponsorshipRequestRepository, never()).save(any());
    }

    @Test
    void getCompanyOffers_ShouldReturnList() {
        when(sponsorshipRequestRepository.findByCompany_IdOrderByDateDesc(11L)).thenReturn(List.of(request));
        when(marketingMapper.toSponsorshipRequestDTO(any(SponsorshipRequest.class))).thenReturn(requestDTO);

        List<SponsorshipRequestDTO> list = service.getCompanyOffers(11L);

        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
    }

    @Test
    void getSponsorInbox_ShouldReturnList() {
        when(sponsorshipRequestRepository.findByStateOrderByDateDesc("PENDING")).thenReturn(List.of(request));
        when(marketingMapper.toSponsorshipRequestDTO(any(SponsorshipRequest.class))).thenReturn(requestDTO);

        List<SponsorshipRequestDTO> list = service.getSponsorInbox();

        assertFalse(list.isEmpty());
    }

    @Test
    void decide_ShouldUpdateAndReturnRequest() {
        when(sponsorshipRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(userRepository.findById(10L)).thenReturn(Optional.of(sponsor));
        when(sponsorshipRequestRepository.save(any(SponsorshipRequest.class))).thenReturn(request);
        when(marketingMapper.toSponsorshipRequestDTO(any(SponsorshipRequest.class))).thenReturn(requestDTO);

        SponsorshipRequestDTO result = service.decide(1L, 10L, true, "http://design.url", "Good");

        assertNotNull(result);
        verify(sponsorshipRequestRepository, times(1)).save(any(SponsorshipRequest.class));
    }
}
