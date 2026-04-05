package tn.esprit.esprit_market.modules.marketing.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tn.esprit.esprit_market.modules.marketing.dto.SponsorshipDTO;
import tn.esprit.esprit_market.modules.marketing.service.ISponsorshipService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SponsorshipControllerTest {

    @Mock
    private ISponsorshipService sponsorshipService;

    @InjectMocks
    private SponsorshipController controller;

    private SponsorshipDTO sponsorshipDTO;

    @BeforeEach
    void setUp() {
        sponsorshipDTO = SponsorshipDTO.builder()
                .id(1L)
                .build();
    }

    @Test
    void testCreateSponsorship() {
        when(sponsorshipService.createSponsorship(any(SponsorshipDTO.class))).thenReturn(sponsorshipDTO);

        ResponseEntity<SponsorshipDTO> res = controller.createSponsorship(sponsorshipDTO);

        assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test
    void testUpdateSponsorship() {
        when(sponsorshipService.updateSponsorship(eq(1L), any(SponsorshipDTO.class))).thenReturn(sponsorshipDTO);

        ResponseEntity<SponsorshipDTO> res = controller.updateSponsorship(1L, sponsorshipDTO);

        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    void testGetSponsorshipById() {
        when(sponsorshipService.getSponsorshipById(1L)).thenReturn(sponsorshipDTO);

        ResponseEntity<SponsorshipDTO> res = controller.getSponsorshipById(1L);

        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    void testGetAllSponsorships() {
        when(sponsorshipService.getAllSponsorships()).thenReturn(Arrays.asList(sponsorshipDTO));

        ResponseEntity<List<SponsorshipDTO>> res = controller.getAllSponsorships();

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(1, res.getBody().size());
    }

    @Test
    void testGetSponsorshipsBySponsor() {
        when(sponsorshipService.getSponsorshipsBySponsor(2L)).thenReturn(Arrays.asList(sponsorshipDTO));

        ResponseEntity<List<SponsorshipDTO>> res = controller.getSponsorshipsBySponsor(2L);

        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    void testGetSponsorshipsByCampaign() {
        when(sponsorshipService.getSponsorshipsByCampaign(3L)).thenReturn(Arrays.asList(sponsorshipDTO));

        ResponseEntity<List<SponsorshipDTO>> res = controller.getSponsorshipsByCampaign(3L);

        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    void testDeleteSponsorship() {
        doNothing().when(sponsorshipService).deleteSponsorship(1L);

        ResponseEntity<Void> res = controller.deleteSponsorship(1L);

        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
        verify(sponsorshipService).deleteSponsorship(1L);
    }
}
