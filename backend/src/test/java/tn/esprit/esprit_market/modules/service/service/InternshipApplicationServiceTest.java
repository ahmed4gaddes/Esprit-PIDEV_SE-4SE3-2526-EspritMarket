package tn.esprit.esprit_market.modules.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.exceptions.UserException;
import tn.esprit.esprit_market.modules.service.dto.ApplyInternshipRequest;
import tn.esprit.esprit_market.modules.service.dto.InternshipApplicationDTO;
import tn.esprit.esprit_market.modules.service.entity.Internship;
import tn.esprit.esprit_market.modules.service.entity.InternshipApplication;
import tn.esprit.esprit_market.modules.service.entity.InternshipApplicationStatus;
import tn.esprit.esprit_market.modules.service.repository.InternshipApplicationRepository;
import tn.esprit.esprit_market.modules.service.repository.InternshipRepository;
import tn.esprit.esprit_market.modules.shared.service.EmailService;
import tn.esprit.esprit_market.modules.shared.service.NotificationService;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InternshipApplicationServiceTest {

    @Mock
    private InternshipApplicationRepository applicationRepository;
    @Mock
    private InternshipRepository internshipRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private InternshipApplicationService applicationService;

    private Internship internship;
    private User applicant;
    private User creator;
    private InternshipApplication application;

    @BeforeEach
    void setUp() {
        creator = new User();
        creator.setId(10L);

        internship = new Internship();
        internship.setId(5L);
        internship.setTitle("Backend Internship");
        internship.setActive(true);
        internship.setCreator(creator);

        applicant = new User();
        applicant.setId(20L);
        applicant.setEmail("applicant@mail.com");

        application = new InternshipApplication();
        application.setId(1L);
        application.setInternship(internship);
        application.setApplicant(applicant);
        application.setStatus(InternshipApplicationStatus.PENDING);
    }

    @Test
    void testApplySuccess() {
        ApplyInternshipRequest req = new ApplyInternshipRequest();
        req.setCvUrl("http://cv.url");

        when(internshipRepository.findById(5L)).thenReturn(Optional.of(internship));
        when(userRepository.findById(20L)).thenReturn(Optional.of(applicant));
        when(applicationRepository.existsByInternship_IdAndApplicant_Id(5L, 20L)).thenReturn(false);
        when(applicationRepository.save(any(InternshipApplication.class))).thenReturn(application);

        InternshipApplicationDTO result = applicationService.apply(5L, 20L, req);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(applicationRepository).save(any(InternshipApplication.class));
    }

    @Test
    void testApplyThrowsUserExceptionWhenNotActive() {
        internship.setActive(false);
        ApplyInternshipRequest req = new ApplyInternshipRequest();

        when(internshipRepository.findById(5L)).thenReturn(Optional.of(internship));

        assertThrows(UserException.class, () -> applicationService.apply(5L, 20L, req));
    }

    @Test
    void testGetApplicationsForInternship() {
        when(internshipRepository.findById(5L)).thenReturn(Optional.of(internship));
        when(applicationRepository.findByInternship_IdOrderByAppliedAtDesc(5L)).thenReturn(Arrays.asList(application));

        List<InternshipApplicationDTO> results = applicationService.getApplicationsForInternship(5L, 10L);

        assertEquals(1, results.size());
    }

    @Test
    void testGetApplicationsForInternshipThrowsUserExceptionForNonCreator() {
        when(internshipRepository.findById(5L)).thenReturn(Optional.of(internship));

        assertThrows(UserException.class, () -> applicationService.getApplicationsForInternship(5L, 11L));
    }

    @Test
    void testDecideAccepted() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
        when(applicationRepository.save(application)).thenReturn(application);

        InternshipApplicationDTO dto = applicationService.decide(1L, 10L, InternshipApplicationStatus.ACCEPTED, "Good");

        assertEquals(InternshipApplicationStatus.ACCEPTED, dto.getStatus());
        assertTrue(internship.isAgreementSigned());
        verify(internshipRepository).save(internship);
        verify(notificationService).notifyUser(eq(20L), anyString(), anyString());
        verify(emailService).sendEmail(eq("applicant@mail.com"), anyString(), anyString());
    }

    @Test
    void testDecideRejected() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
        when(applicationRepository.save(application)).thenReturn(application);

        InternshipApplicationDTO dto = applicationService.decide(1L, 10L, InternshipApplicationStatus.REJECTED, "Not fit");

        assertEquals(InternshipApplicationStatus.REJECTED, dto.getStatus());
        verify(notificationService).notifyUser(eq(20L), anyString(), anyString());
        verify(emailService).sendEmail(eq("applicant@mail.com"), anyString(), anyString());
    }
}
