package tn.esprit.esprit_market.modules.service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.service.dto.ApplyInternshipRequest;
import tn.esprit.esprit_market.modules.service.dto.InternshipApplicationDTO;
import tn.esprit.esprit_market.modules.service.dto.InternshipApplicationDecisionRequest;
import tn.esprit.esprit_market.modules.service.service.InternshipApplicationService;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.service.IUserService;

import java.util.List;

@RestController
@RequestMapping("/api/internship-applications")
@RequiredArgsConstructor
public class InternshipApplicationController {

    private final InternshipApplicationService internshipApplicationService;
    private final IUserService userService;

    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @PostMapping("/internships/{internshipId}/apply")
    public ResponseEntity<InternshipApplicationDTO> apply(
            @PathVariable Long internshipId,
            @Valid @RequestBody ApplyInternshipRequest request,
            Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(internshipApplicationService.apply(internshipId, user.getId(), request));
    }

    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @GetMapping("/me")
    public ResponseEntity<List<InternshipApplicationDTO>> myApplications(Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(internshipApplicationService.getMyApplications(user.getId()));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_COMPANY', 'ROLE_EXPERT')")
    @GetMapping("/internships/{internshipId}")
    public ResponseEntity<List<InternshipApplicationDTO>> byInternship(
            @PathVariable Long internshipId,
            Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(internshipApplicationService.getApplicationsForInternship(internshipId, user.getId()));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_COMPANY', 'ROLE_EXPERT')")
    @PutMapping("/{applicationId}/status")
    public ResponseEntity<InternshipApplicationDTO> decide(
            @PathVariable Long applicationId,
            @Valid @RequestBody InternshipApplicationDecisionRequest request,
            Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(
                internshipApplicationService.decide(
                        applicationId,
                        user.getId(),
                        request.getStatus(),
                        request.getReviewerComment()));
    }
}
