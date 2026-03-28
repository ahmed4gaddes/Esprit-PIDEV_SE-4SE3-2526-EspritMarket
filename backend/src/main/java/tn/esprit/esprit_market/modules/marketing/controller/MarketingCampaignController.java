package tn.esprit.esprit_market.modules.marketing.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.marketing.dto.MarketingCampaignDTO;
import tn.esprit.esprit_market.modules.marketing.service.IMarketingCampaignService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin/marketing-campaigns")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MarketingCampaignController {

    private final IMarketingCampaignService marketingCampaignService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<MarketingCampaignDTO> createCampaign(@Valid @RequestBody MarketingCampaignDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(marketingCampaignService.createCampaign(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<MarketingCampaignDTO> updateCampaign(@PathVariable Long id, @Valid @RequestBody MarketingCampaignDTO dto) {
        return ResponseEntity.ok(marketingCampaignService.updateCampaign(id, dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<MarketingCampaignDTO> getCampaignById(@PathVariable Long id) {
        return ResponseEntity.ok(marketingCampaignService.getCampaignById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<MarketingCampaignDTO>> getAllCampaigns() {
        return ResponseEntity.ok(marketingCampaignService.getAllCampaigns());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteCampaign(@PathVariable Long id) {
        marketingCampaignService.deleteCampaign(id);
        return ResponseEntity.noContent().build();
    }
}
