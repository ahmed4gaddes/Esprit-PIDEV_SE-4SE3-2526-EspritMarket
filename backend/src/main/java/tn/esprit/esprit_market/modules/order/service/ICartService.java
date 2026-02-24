package tn.esprit.esprit_market.modules.order.service;

import tn.esprit.esprit_market.modules.order.dto.AddToCartRequest;
import tn.esprit.esprit_market.modules.order.dto.CartResponse;

public interface ICartService {
    CartResponse getCartByUserId(Long userId);
    CartResponse addItem(Long userId, AddToCartRequest request);
    CartResponse updateItemQuantity(Long userId, Long cartItemId, int quantity);
    CartResponse removeItem(Long userId, Long cartItemId);
    void clearCart(Long userId);
    double getCartTotal(Long userId);
}