package tn.esprit.esprit_market.modules.marketing.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.marketing.entity.SponsorshipRequest;
import tn.esprit.esprit_market.modules.marketing.service.SponsorshipRequestService;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.enums.Role;
import tn.esprit.esprit_market.modules.user.service.IUserService;

import java.util.List;

@RestController
@RequestMapping("/api/sponsorship-requests")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SponsorshipRequestController {

    private final SponsorshipRequestService sponsorshipRequestService;
    private final IUserService userService;

    @PostMapping
    public ResponseEntity<SponsorshipRequest> createOffer(@RequestBody SponsorshipRequest request, Authentication authentication) {
        User currentUser = userService.getUserByEmail(authentication.getName());
        if (currentUser.getRole() != Role.COMPANY) {
            throw new AccessDeniedException("Only COMPANY can create offers.");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sponsorshipRequestService.createOffer(currentUser.getId(), request));
    }

    @GetMapping("/company/me")
    public ResponseEntity<List<SponsorshipRequest>> getMyOffers(Authentication authentication) {
        User currentUser = userService.getUserByEmail(authentication.getName());
        if (currentUser.getRole() != Role.COMPANY) {
            throw new AccessDeniedException("Only COMPANY can access this endpoint.");
        }
        return ResponseEntity.ok(sponsorshipRequestService.getCompanyOffers(currentUser.getId()));
    }

    @GetMapping("/sponsor/inbox")
    public ResponseEntity<List<SponsorshipRequest>> getSponsorInbox(Authentication authentication) {
        User currentUser = userService.getUserByEmail(authentication.getName());
        if (currentUser.getRole() != Role.SPONSOR) {
            throw new AccessDeniedException("Only SPONSOR can access this endpoint.");
        }
        return ResponseEntity.ok(sponsorshipRequestService.getSponsorInbox());
    }

    @GetMapping("/sponsor/history")
    public ResponseEntity<List<SponsorshipRequest>> getSponsorHistory(Authentication authentication) {
        User currentUser = userService.getUserByEmail(authentication.getName());
        if (currentUser.getRole() != Role.SPONSOR) {
            throw new AccessDeniedException("Only SPONSOR can access this endpoint.");
        }
        return ResponseEntity.ok(sponsorshipRequestService.getSponsorHistory(currentUser.getId()));
    }

    @PutMapping("/{id}/decision")
    public ResponseEntity<SponsorshipRequest> decide(@PathVariable Long id,
                                                     @RequestBody DecisionRequest body,
                                                     Authentication authentication) {
        User currentUser = userService.getUserByEmail(authentication.getName());
        if (currentUser.getRole() != Role.SPONSOR) {
            throw new AccessDeniedException("Only SPONSOR can decide offers.");
        }
        return ResponseEntity.ok(sponsorshipRequestService.decide(
                id,
                currentUser.getId(),
                body.approved,
                body.designUrl,
                body.note
        ));
    }

    @GetMapping("/public/approved")
    public ResponseEntity<List<SponsorshipRequest>> getApprovedForCustomers() {
        return ResponseEntity.ok(sponsorshipRequestService.getApprovedAdsForCustomers());
    }

    public static class DecisionRequest {
        public boolean approved;
        public String designUrl;
        public String note;
    }
}

