package tn.esprit.esprit_market.modules.store.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.store.dto.ProductRecommendationDTO;
import tn.esprit.esprit_market.modules.store.service.RecommendationService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "http://localhost:4200")
@AllArgsConstructor
public class RecommendationController {

    private RecommendationService recommendationService;

    // Recommandations pour un produit
    @GetMapping("/product/{id}")
    public ResponseEntity<Map<String, List<ProductRecommendationDTO>>>
    getRecommendations(@PathVariable Long id) {
        return ResponseEntity.ok(
                recommendationService.getFullRecommendations(id)
        );
    }

    // Best sellers
    @GetMapping("/best-sellers")
    public ResponseEntity<List<ProductRecommendationDTO>> getBestSellers() {
        return ResponseEntity.ok(
                recommendationService.getBestSellers()
        );
    }
}