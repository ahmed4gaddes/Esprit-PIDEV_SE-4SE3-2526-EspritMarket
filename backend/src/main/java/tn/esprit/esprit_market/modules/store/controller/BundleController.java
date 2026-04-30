package tn.esprit.esprit_market.modules.store.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.store.service.BundleService;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/bundle")
@CrossOrigin("*")
public class BundleController {


    private BundleService bundleService;

    // Par ID produit
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Map<String, Object>>> getByProductId(
            @PathVariable Long productId) {
        return ResponseEntity.ok(bundleService.getBundleSuggestions(productId));
    }

    // Par nom produit (pour le chatbot)
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> getByName(
            @RequestParam String name) {
        return ResponseEntity.ok(bundleService.getBundlesByProductName(name));
    }

    // Tous les bundles populaires
    @GetMapping("/all")
    public ResponseEntity<List<Map<String, Object>>> getAll() {
        return ResponseEntity.ok(bundleService.getAllPopularBundles());
    }
}
