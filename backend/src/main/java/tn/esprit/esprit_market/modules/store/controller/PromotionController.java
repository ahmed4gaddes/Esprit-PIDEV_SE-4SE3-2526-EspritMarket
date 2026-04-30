package tn.esprit.esprit_market.modules.store.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.store.entity.Promotion;
import tn.esprit.esprit_market.modules.store.repository.PromotionRepository;

import java.util.List;

@RestController
@RequestMapping("Promotion")
@AllArgsConstructor
public class PromotionController {

    private final PromotionRepository promotionRepository;

    @PostMapping("/add")
    public ResponseEntity<Promotion> createPromotion(@RequestBody Promotion promotion) {
        return ResponseEntity.ok(promotionRepository.save(promotion));
    }

    @GetMapping("/getall")
    public ResponseEntity<List<Promotion>> getAllPromotions() {
        return ResponseEntity.ok(promotionRepository.findAll());
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<Promotion>> getPromotionsByStore(@PathVariable Long storeId) {
        return ResponseEntity.ok(promotionRepository.findByStoreId(storeId));
    }

    @GetMapping("/validate/{code}")
    public ResponseEntity<?> validateCode(@PathVariable String code) {
        return promotionRepository.findByCode(code)
                .map(promo -> {
                    if (promo.getExpiresAt() != null && promo.getExpiresAt().before(new java.util.Date())) {
                        return ResponseEntity.badRequest().body("Code promo expiré.");
                    }
                    if (promo.getUsageLimit() > 0 && promo.getUsedCount() >= promo.getUsageLimit()) {
                        return ResponseEntity.badRequest().body("Limite d'utilisation atteinte.");
                    }
                    return ResponseEntity.ok(promo);
                })
                .orElse(ResponseEntity.badRequest().body("Code promo invalide."));
    }
}
