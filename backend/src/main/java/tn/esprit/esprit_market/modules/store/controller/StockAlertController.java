package tn.esprit.esprit_market.modules.store.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.store.entity.StockAlert;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryProduct;
import tn.esprit.esprit_market.modules.store.repository.StockAlertRepository;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

@RestController
@RequestMapping("StockAlert")
@AllArgsConstructor
public class StockAlertController {

    private final StockAlertRepository stockAlertRepository;
    private final IRepositoryProduct productRepository;
    private final UserRepository userRepository;

    @PostMapping("/notify-me/{productId}")
    public ResponseEntity<?> subscribeToAlert(@PathVariable Long productId) {
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        if (stockAlertRepository.existsByUserIdAndProductIdAndNotifiedFalse(user.getId(), product.getId())) {
            return ResponseEntity.badRequest().body("Vous êtes déjà inscrit pour cette alerte.");
        }

        StockAlert alert = StockAlert.builder()
                .user(user)
                .product(product)
                .build();
        stockAlertRepository.save(alert);

        return ResponseEntity.ok("Vous serez notifié dès que le produit sera de nouveau en stock.");
    }
}
