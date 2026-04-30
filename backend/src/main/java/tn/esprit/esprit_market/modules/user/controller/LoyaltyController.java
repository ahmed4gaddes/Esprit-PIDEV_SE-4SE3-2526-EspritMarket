package tn.esprit.esprit_market.modules.user.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.LoyaltyPointsRepository;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.Map;

@RestController
@RequestMapping("Loyalty")
@AllArgsConstructor
public class LoyaltyController {

    private final LoyaltyPointsRepository loyaltyPointsRepository;
    private final UserRepository userRepository;

    @GetMapping("/my")
    public ResponseEntity<?> getMyLoyaltyPoints() {
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        
        return loyaltyPointsRepository.findByUserId(user.getId())
                .map(lp -> ResponseEntity.ok(Map.of("availablePoints", lp.getAvailablePoints(), "totalPoints", lp.getTotalPoints())))
                .orElse(ResponseEntity.ok(Map.of("availablePoints", 0, "totalPoints", 0)));
    }
}
