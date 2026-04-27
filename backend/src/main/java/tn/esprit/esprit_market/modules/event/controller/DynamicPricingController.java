package tn.esprit.esprit_market.modules.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.event.dto.DynamicPriceResponse;
import tn.esprit.esprit_market.modules.event.dto.PricingRuleRequest;
import tn.esprit.esprit_market.modules.event.entities.PricingRule;
import tn.esprit.esprit_market.modules.event.service.DynamicPricingService;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class DynamicPricingController {

    private final DynamicPricingService dynamicPricingService;

    // =====================================================================
    // GET /api/events/{eventId}/current-price — PUBLIC
    // Returns the current dynamic price for an event
    // =====================================================================
    @GetMapping("/{eventId}/current-price")
    public ResponseEntity<DynamicPriceResponse> getCurrentPrice(@PathVariable Long eventId) {
        DynamicPriceResponse response = dynamicPricingService.calculateCurrentPrice(eventId);
        return ResponseEntity.ok(response);
    }

    // =====================================================================
    // POST /api/events/{eventId}/pricing-rule — EXPERT / SELLER / ADMIN
    // Creates a new pricing rule for an event
    // =====================================================================
    @PostMapping("/{eventId}/pricing-rule")
    public ResponseEntity<PricingRule> createPricingRule(
            @PathVariable Long eventId,
            @Valid @RequestBody PricingRuleRequest request,
            Authentication authentication) {
        PricingRule rule = dynamicPricingService.createPricingRule(
                eventId, request, authentication.getName());
        return new ResponseEntity<>(rule, HttpStatus.CREATED);
    }

    // =====================================================================
    // PUT /api/events/{eventId}/pricing-rule — EXPERT / SELLER / ADMIN
    // Updates the existing pricing rule for an event
    // =====================================================================
    @PutMapping("/{eventId}/pricing-rule")
    public ResponseEntity<PricingRule> updatePricingRule(
            @PathVariable Long eventId,
            @Valid @RequestBody PricingRuleRequest request,
            Authentication authentication) {
        PricingRule rule = dynamicPricingService.updatePricingRule(
                eventId, request, authentication.getName());
        return ResponseEntity.ok(rule);
    }

    // =====================================================================
    // GET /api/events/{eventId}/pricing-rule — EXPERT / SELLER / ADMIN
    // Gets the pricing rule configuration for an event
    // =====================================================================
    @GetMapping("/{eventId}/pricing-rule")
    public ResponseEntity<PricingRule> getPricingRule(@PathVariable Long eventId) {
        PricingRule rule = dynamicPricingService.getPricingRule(eventId);
        return ResponseEntity.ok(rule);
    }

    // =====================================================================
    // DELETE /api/events/{eventId}/pricing-rule — EXPERT / SELLER / ADMIN
    // Deletes the pricing rule (reverts to fixed pricing)
    // =====================================================================
    @DeleteMapping("/{eventId}/pricing-rule")
    public ResponseEntity<Void> deletePricingRule(
            @PathVariable Long eventId,
            Authentication authentication) {
        dynamicPricingService.deletePricingRule(eventId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
