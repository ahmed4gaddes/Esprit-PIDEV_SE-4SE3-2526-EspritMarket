package tn.esprit.esprit_market.modules.marketing.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.marketing.dto.AdvertisementDTO;
import tn.esprit.esprit_market.modules.marketing.service.IAdvertisementService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin/advertisements")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdvertisementController {

    private final IAdvertisementService advertisementService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<AdvertisementDTO> createAdvertisement(@Valid @RequestBody AdvertisementDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(advertisementService.createAdvertisement(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<AdvertisementDTO> updateAdvertisement(@PathVariable Long id, @Valid @RequestBody AdvertisementDTO dto) {
        return ResponseEntity.ok(advertisementService.updateAdvertisement(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdvertisementDTO> getAdvertisementById(@PathVariable Long id) {
        return ResponseEntity.ok(advertisementService.getAdvertisementById(id));
    }

    @GetMapping
    public ResponseEntity<List<AdvertisementDTO>> getAllAdvertisements() {
        return ResponseEntity.ok(advertisementService.getAllAdvertisements());
    }
    
    @GetMapping("/campaign/{campaignId}")
    public ResponseEntity<List<AdvertisementDTO>> getAdvertisementsByCampaign(@PathVariable Long campaignId) {
        return ResponseEntity.ok(advertisementService.getAdvertisementsByCampaign(campaignId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteAdvertisement(@PathVariable Long id) {
        advertisementService.deleteAdvertisement(id);
        return ResponseEntity.noContent().build();
    }
}
