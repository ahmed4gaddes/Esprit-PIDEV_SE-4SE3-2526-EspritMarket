package tn.esprit.esprit_market.modules.marketing.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.marketing.dto.SponsorshipDTO;
import tn.esprit.esprit_market.modules.marketing.service.ISponsorshipService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/sponsorships")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SponsorshipController {

    private final ISponsorshipService sponsorshipService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SponsorshipDTO> createSponsorship(@RequestBody SponsorshipDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sponsorshipService.createSponsorship(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SponsorshipDTO> updateSponsorship(@PathVariable Long id, @RequestBody SponsorshipDTO dto) {
        return ResponseEntity.ok(sponsorshipService.updateSponsorship(id, dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SponsorshipDTO> getSponsorshipById(@PathVariable Long id) {
        return ResponseEntity.ok(sponsorshipService.getSponsorshipById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<SponsorshipDTO>> getAllSponsorships() {
        return ResponseEntity.ok(sponsorshipService.getAllSponsorships());
    }

    @GetMapping("/sponsor/{sponsorId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<SponsorshipDTO>> getSponsorshipsBySponsor(@PathVariable Long sponsorId) {
        return ResponseEntity.ok(sponsorshipService.getSponsorshipsBySponsor(sponsorId));
    }

    @GetMapping("/campaign/{campaignId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<SponsorshipDTO>> getSponsorshipsByCampaign(@PathVariable Long campaignId) {
        return ResponseEntity.ok(sponsorshipService.getSponsorshipsByCampaign(campaignId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteSponsorship(@PathVariable Long id) {
        sponsorshipService.deleteSponsorship(id);
        return ResponseEntity.noContent().build();
    }
}
