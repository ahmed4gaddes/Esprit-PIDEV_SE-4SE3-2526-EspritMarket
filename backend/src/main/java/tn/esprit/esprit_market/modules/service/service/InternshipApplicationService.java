package tn.esprit.esprit_market.modules.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
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

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InternshipApplicationService {

    private final InternshipApplicationRepository applicationRepository;
    private final InternshipRepository internshipRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;

    public InternshipApplicationDTO apply(Long internshipId, Long applicantId, ApplyInternshipRequest request) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Internship not found with id: " + internshipId));

        if (!internship.isActive()) {
            throw new UserException("This internship is not active.");
        }

        User applicant = userRepository.findById(applicantId)
                .orElseThrow(() -> new ResourceNotFoundException("Applicant not found with id: " + applicantId));

        if (applicationRepository.existsByInternship_IdAndApplicant_Id(internshipId, applicantId)) {
            throw new UserException("You already applied for this internship.");
        }

        InternshipApplication app = new InternshipApplication();
        app.setInternship(internship);
        app.setApplicant(applicant);
        app.setCvUrl(request.getCvUrl());
        app.setCoverLetter(request.getCoverLetter());
        app.setStatus(InternshipApplicationStatus.PENDING);
        app.setAppliedAt(new Date());

        return toDto(applicationRepository.save(app));
    }

    @Transactional(readOnly = true)
    public List<InternshipApplicationDTO> getMyApplications(Long applicantId) {
        return applicationRepository.findByApplicant_IdOrderByAppliedAtDesc(applicantId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InternshipApplicationDTO> getApplicationsForInternship(Long internshipId, Long companyUserId) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Internship not found with id: " + internshipId));

        if (internship.getCreator() == null || !internship.getCreator().getId().equals(companyUserId)) {
            throw new UserException("You are not allowed to view applications for this internship.");
        }

        return applicationRepository.findByInternship_IdOrderByAppliedAtDesc(internshipId).stream()
                .map(this::toDto)
                .toList();
    }

    public InternshipApplicationDTO decide(Long applicationId, Long companyUserId, InternshipApplicationStatus status, String comment) {
        InternshipApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        Internship internship = application.getInternship();
        if (internship.getCreator() == null || !internship.getCreator().getId().equals(companyUserId)) {
            throw new UserException("You are not allowed to review this application.");
        }

        if (status == InternshipApplicationStatus.PENDING) {
            throw new UserException("Decision status cannot be PENDING.");
        }

        application.setStatus(status);
        application.setReviewedAt(new Date());
        application.setReviewerComment(comment);
        if (status == InternshipApplicationStatus.ACCEPTED) {
            internship.setAgreementSigned(true);
            internshipRepository.save(internship);
        }
        InternshipApplication saved = applicationRepository.save(application);

        Long applicantUserId = application.getApplicant() != null ? application.getApplicant().getId() : null;
        if (applicantUserId != null) {
            if (status == InternshipApplicationStatus.ACCEPTED) {
                notificationService.notifyUser(
                        applicantUserId,
                        "Internship application accepted",
                        "Your application for \"" + internship.getTitle() + "\" is accepted. Merci de contacter via mail.");
            } else {
                notificationService.notifyUser(
                        applicantUserId,
                        "Internship application rejected",
                        "Your application for \"" + internship.getTitle() + "\" is rejected.");
            }
        }

        String applicantEmail = application.getApplicant().getEmail();
        if (applicantEmail != null && !applicantEmail.isBlank()) {
            String subject = "Internship application update";
            String body;
            if (status == InternshipApplicationStatus.ACCEPTED) {
                body = "Congratulations! Your application for internship \"" + internship.getTitle() + "\" has been accepted.";
            } else {
                body = "Your application for internship \"" + internship.getTitle() + "\" has been rejected.";
            }
            if (comment != null && !comment.isBlank()) {
                body += "\n\nReviewer note: " + comment;
            }
            try {
                emailService.sendEmail(applicantEmail, subject, body);
            } catch (Exception e) {
                // Do not fail the business action if SMTP provider is temporarily unavailable/rate-limited.
                log.warn("Application {} status updated but email could not be sent to {}: {}",
                        application.getId(), applicantEmail, e.getMessage());
            }
        }

        return toDto(saved);
    }

    private InternshipApplicationDTO toDto(InternshipApplication app) {
        InternshipApplicationDTO dto = new InternshipApplicationDTO();
        dto.setId(app.getId());
        dto.setInternshipId(app.getInternship() != null ? app.getInternship().getId() : null);
        dto.setInternshipTitle(app.getInternship() != null ? app.getInternship().getTitle() : null);
        dto.setApplicantId(app.getApplicant() != null ? app.getApplicant().getId() : null);
        dto.setApplicantName(app.getApplicant() != null ? app.getApplicant().getName() : null);
        dto.setApplicantEmail(app.getApplicant() != null ? app.getApplicant().getEmail() : null);
        dto.setCvUrl(app.getCvUrl());
        dto.setCoverLetter(app.getCoverLetter());
        dto.setStatus(app.getStatus());
        dto.setAppliedAt(app.getAppliedAt());
        dto.setReviewedAt(app.getReviewedAt());
        dto.setReviewerComment(app.getReviewerComment());
        return dto;
    }
}
