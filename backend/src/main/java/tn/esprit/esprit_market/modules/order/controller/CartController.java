package tn.esprit.esprit_market.modules.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.order.dto.CartItemRequestDTO;
import tn.esprit.esprit_market.modules.order.dto.CartResponseDTO;
import tn.esprit.esprit_market.modules.order.service.ICartService;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final ICartService cartService;

    @GetMapping
    public ResponseEntity<CartResponseDTO> getCart(Authentication authentication) {
        return ResponseEntity.ok(cartService.getCart(authentication.getName()));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponseDTO> addItemToCart(
            @Valid @RequestBody CartItemRequestDTO request,
            Authentication authentication) {
        return ResponseEntity.ok(cartService.addItemToCart(authentication.getName(), request));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponseDTO> updateItemQuantity(
            @PathVariable Long itemId,
            @RequestParam int quantity,
            Authentication authentication) {
        return ResponseEntity.ok(cartService.updateItemQuantity(authentication.getName(), itemId, quantity));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponseDTO> removeItemFromCart(
            @PathVariable Long itemId,
            Authentication authentication) {
        return ResponseEntity.ok(cartService.removeItemFromCart(authentication.getName(), itemId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<CartResponseDTO> clearCart(Authentication authentication) {
        return ResponseEntity.ok(cartService.clearCart(authentication.getName()));
    }
}
