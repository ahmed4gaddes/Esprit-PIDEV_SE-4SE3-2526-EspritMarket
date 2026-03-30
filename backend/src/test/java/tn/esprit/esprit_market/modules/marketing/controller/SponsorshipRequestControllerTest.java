package tn.esprit.esprit_market.modules.marketing.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import tn.esprit.esprit_market.modules.marketing.dto.SponsorshipDecisionDTO;
import tn.esprit.esprit_market.modules.marketing.dto.SponsorshipRequestDTO;
import tn.esprit.esprit_market.modules.marketing.entity.SponsorshipRequest;
import tn.esprit.esprit_market.modules.marketing.service.ISponsorshipRequestService;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.enums.Role;
import tn.esprit.esprit_market.modules.user.service.IUserService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SponsorshipRequestControllerTest {

    @Mock
    private ISponsorshipRequestService sponsorshipRequestService;

    @Mock
    private IUserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private SponsorshipRequestController controller;

    private User companyUser;
    private User sponsorUser;
    private User customerUser;
    private SponsorshipRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        companyUser = new User();
        companyUser.setId(1L);
        companyUser.setEmail("company@mail.com");
        companyUser.setRole(Role.COMPANY);

        sponsorUser = new User();
        sponsorUser.setId(2L);
        sponsorUser.setEmail("sponsor@mail.com");
        sponsorUser.setRole(Role.SPONSOR);

        customerUser = new User();
        customerUser.setId(3L);
        customerUser.setEmail("customer@mail.com");
        customerUser.setRole(Role.CUSTOMER);

        requestDTO = SponsorshipRequestDTO.builder()
                .id(1L)
                .offerTitle("Offre partenariat")
                .budget(5000.0)
                .state("PENDING")
                .build();
    }

    @Test
    void testCreateOffer_Success() {
        when(authentication.getName()).thenReturn("company@mail.com");
        when(userService.getUserByEmail("company@mail.com")).thenReturn(companyUser);
        when(sponsorshipRequestService.createOffer(eq(1L), any(SponsorshipRequest.class))).thenReturn(requestDTO);

        ResponseEntity<SponsorshipRequestDTO> res = controller.createOffer(new SponsorshipRequest(), authentication);

        assertEquals(HttpStatus.CREATED, res.getStatusCode());
        assertNotNull(res.getBody());
    }

    @Test
    void testCreateOffer_NonCompany_ThrowsAccessDenied() {
        when(authentication.getName()).thenReturn("customer@mail.com");
        when(userService.getUserByEmail("customer@mail.com")).thenReturn(customerUser);

        assertThrows(AccessDeniedException.class, () ->
                controller.createOffer(new SponsorshipRequest(), authentication));
    }

    @Test
    void testGetMyOffers() {
        when(authentication.getName()).thenReturn("company@mail.com");
        when(userService.getUserByEmail("company@mail.com")).thenReturn(companyUser);
        when(sponsorshipRequestService.getCompanyOffers(1L)).thenReturn(Arrays.asList(requestDTO));

        ResponseEntity<List<SponsorshipRequestDTO>> res = controller.getMyOffers(authentication);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(1, res.getBody().size());
    }

    @Test
    void testGetSponsorInbox() {
        when(authentication.getName()).thenReturn("sponsor@mail.com");
        when(userService.getUserByEmail("sponsor@mail.com")).thenReturn(sponsorUser);
        when(sponsorshipRequestService.getSponsorInbox()).thenReturn(Arrays.asList(requestDTO));

        ResponseEntity<List<SponsorshipRequestDTO>> res = controller.getSponsorInbox(authentication);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(1, res.getBody().size());
    }

    @Test
    void testGetSponsorInbox_NonSponsor_ThrowsAccessDenied() {
        when(authentication.getName()).thenReturn("customer@mail.com");
        when(userService.getUserByEmail("customer@mail.com")).thenReturn(customerUser);

        assertThrows(AccessDeniedException.class, () ->
                controller.getSponsorInbox(authentication));
    }

    @Test
    void testGetSponsorHistory() {
        when(authentication.getName()).thenReturn("sponsor@mail.com");
        when(userService.getUserByEmail("sponsor@mail.com")).thenReturn(sponsorUser);
        when(sponsorshipRequestService.getSponsorHistory(2L)).thenReturn(Arrays.asList(requestDTO));

        ResponseEntity<List<SponsorshipRequestDTO>> res = controller.getSponsorHistory(authentication);

        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    void testDecide() {
        SponsorshipDecisionDTO decision = new SponsorshipDecisionDTO();
        decision.approved = true;
        decision.designUrl = "http://img.com/design.png";
        decision.note = "Accepté";

        when(authentication.getName()).thenReturn("sponsor@mail.com");
        when(userService.getUserByEmail("sponsor@mail.com")).thenReturn(sponsorUser);
        when(sponsorshipRequestService.decide(eq(1L), eq(2L), eq(true), anyString(), anyString())).thenReturn(requestDTO);

        ResponseEntity<SponsorshipRequestDTO> res = controller.decide(1L, decision, authentication);

        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    void testGetApprovedForCustomers() {
        when(sponsorshipRequestService.getApprovedAdsForCustomers()).thenReturn(Arrays.asList(requestDTO));

        ResponseEntity<List<SponsorshipRequestDTO>> res = controller.getApprovedForCustomers();

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(1, res.getBody().size());
    }
}
