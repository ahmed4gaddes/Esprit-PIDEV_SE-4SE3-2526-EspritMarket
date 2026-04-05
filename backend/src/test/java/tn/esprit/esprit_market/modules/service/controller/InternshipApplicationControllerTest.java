package tn.esprit.esprit_market.modules.service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import tn.esprit.esprit_market.modules.service.dto.InternshipApplicationDTO;
import tn.esprit.esprit_market.modules.service.dto.ApplyInternshipRequest;
import tn.esprit.esprit_market.modules.service.dto.InternshipApplicationDecisionRequest;
import tn.esprit.esprit_market.modules.service.entity.InternshipApplicationStatus;
import tn.esprit.esprit_market.modules.service.service.InternshipApplicationService;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.service.IUserService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InternshipApplicationControllerTest {

    @Mock private InternshipApplicationService internshipApplicationService;
    @Mock private IUserService userService;
    @Mock private Authentication authentication;
    @InjectMocks private InternshipApplicationController controller;
    private InternshipApplicationDTO dto;
    private User user;

    @BeforeEach
    void setUp() {
        dto = new InternshipApplicationDTO(); dto.setId(1L);
        user = new User(); user.setId(1L); user.setEmail("user@mail.com");
    }

    @Test void testApply() {
        when(authentication.getName()).thenReturn("user@mail.com");
        when(userService.getUserByEmail("user@mail.com")).thenReturn(user);
        ApplyInternshipRequest request = new ApplyInternshipRequest();
        when(internshipApplicationService.apply(eq(1L), eq(1L), any(ApplyInternshipRequest.class))).thenReturn(dto);
        ResponseEntity<InternshipApplicationDTO> res = controller.apply(1L, request, authentication);
        assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test void testMyApplications() {
        when(authentication.getName()).thenReturn("user@mail.com");
        when(userService.getUserByEmail("user@mail.com")).thenReturn(user);
        when(internshipApplicationService.getMyApplications(1L)).thenReturn(Arrays.asList(dto));
        ResponseEntity<List<InternshipApplicationDTO>> res = controller.myApplications(authentication);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testByInternship() {
        when(authentication.getName()).thenReturn("user@mail.com");
        when(userService.getUserByEmail("user@mail.com")).thenReturn(user);
        when(internshipApplicationService.getApplicationsForInternship(1L, 1L)).thenReturn(Arrays.asList(dto));
        ResponseEntity<List<InternshipApplicationDTO>> res = controller.byInternship(1L, authentication);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testDecide() {
        when(authentication.getName()).thenReturn("user@mail.com");
        when(userService.getUserByEmail("user@mail.com")).thenReturn(user);
        InternshipApplicationDecisionRequest request = new InternshipApplicationDecisionRequest();
        request.setStatus(InternshipApplicationStatus.ACCEPTED);
        request.setReviewerComment("Good candidate");
        when(internshipApplicationService.decide(eq(1L), eq(1L), eq(InternshipApplicationStatus.ACCEPTED), eq("Good candidate"))).thenReturn(dto);
        ResponseEntity<InternshipApplicationDTO> res = controller.decide(1L, request, authentication);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }
}
