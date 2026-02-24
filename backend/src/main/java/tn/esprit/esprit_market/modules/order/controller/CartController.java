package tn.esprit.esprit_market.modules.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.order.dto.AddToCartRequest;
import tn.esprit.esprit_market.modules.order.dto.CartResponse;
import tn.esprit.esprit_market.modules.order.service.ICartService;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final ICartService cartService;
    private final UserRepository userRepository;

    private Long getLoggedInUserId(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getId();
    }

    @GetMapping("/my")
    public ResponseEntity<CartResponse> getMyCart(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.getCartByUserId(getLoggedInUserId(userDetails)));
    }

    @PostMapping("/my/items")
    public ResponseEntity<CartResponse> addItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(cartService.addItem(getLoggedInUserId(userDetails), request));
    }

    @PutMapping("/my/items/{cartItemId}")
    public ResponseEntity<CartResponse> updateItemQuantity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long cartItemId,
            @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.updateItemQuantity(getLoggedInUserId(userDetails), cartItemId, quantity));
    }

    @DeleteMapping("/my/items/{cartItemId}")
    public ResponseEntity<CartResponse> removeItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long cartItemId) {
        return ResponseEntity.ok(cartService.removeItem(getLoggedInUserId(userDetails), cartItemId));
    }

    @DeleteMapping("/my/clear")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        cartService.clearCart(getLoggedInUserId(userDetails));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my/total")
    public ResponseEntity<Double> getCartTotal(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.getCartTotal(getLoggedInUserId(userDetails)));
    }
}