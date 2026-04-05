package tn.esprit.esprit_market.modules.order.service;

import tn.esprit.esprit_market.modules.order.dto.CartItemRequestDTO;
import tn.esprit.esprit_market.modules.order.dto.CartResponseDTO;

public interface ICartService {
    CartResponseDTO getCart(String userEmail);
    CartResponseDTO addItemToCart(String userEmail, CartItemRequestDTO request);
    CartResponseDTO updateItemQuantity(String userEmail, Long cartItemId, int quantity);
    CartResponseDTO removeItemFromCart(String userEmail, Long cartItemId);
    CartResponseDTO clearCart(String userEmail);
}
