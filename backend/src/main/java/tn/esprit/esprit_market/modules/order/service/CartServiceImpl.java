package tn.esprit.esprit_market.modules.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.order.dto.AddToCartRequest;
import tn.esprit.esprit_market.modules.order.dto.CartItemResponse;
import tn.esprit.esprit_market.modules.order.dto.CartResponse;
import tn.esprit.esprit_market.modules.order.entity.Cart;
import tn.esprit.esprit_market.modules.order.entity.CartItem;
import tn.esprit.esprit_market.modules.order.repository.CartItemRepository;
import tn.esprit.esprit_market.modules.order.repository.CartRepository;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements ICartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    private Cart getOrCreateCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = Cart.builder()
                    .user(user)
                    .createdAt(new Date())
                    .build();
            return cartRepository.save(newCart);
        });
    }

    @Override
    public CartResponse getCartByUserId(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return mapToResponse(cart);
    }

    @Override
    public CartResponse addItem(Long userId, AddToCartRequest request) {
        if (request.getProductId() == null && request.getServiceId() == null)
            throw new IllegalArgumentException("Either productId or serviceId must be provided");
        if (request.getProductId() != null && request.getServiceId() != null)
            throw new IllegalArgumentException("Only one of productId or serviceId can be provided");

        Cart cart = getOrCreateCart(userId);

        if (request.getProductId() != null) {
            Optional<CartItem> existing = cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId());
            if (existing.isPresent()) {
                existing.get().setQuantity(existing.get().getQuantity() + request.getQuantity());
                cartItemRepository.save(existing.get());
                return mapToResponse(cartRepository.findById(cart.getId()).orElse(cart));
            }
        } else {
            Optional<CartItem> existing = cartItemRepository.findByCartIdAndServiceId(cart.getId(), request.getServiceId());
            if (existing.isPresent()) {
                existing.get().setQuantity(existing.get().getQuantity() + request.getQuantity());
                cartItemRepository.save(existing.get());
                return mapToResponse(cartRepository.findById(cart.getId()).orElse(cart));
            }
        }

        CartItem.CartItemBuilder itemBuilder = CartItem.builder()
                .cart(cart)
                .quantity(request.getQuantity())
                .unitPrice(0);

        if (request.getProductId() != null) {
            tn.esprit.esprit_market.modules.store.entity.Product product =
                    new tn.esprit.esprit_market.modules.store.entity.Product();
            product.setId(request.getProductId());
            itemBuilder.product(product);
        } else {
            tn.esprit.esprit_market.modules.service.entity.Service service =
                    new tn.esprit.esprit_market.modules.service.entity.Service();
            service.setId(request.getServiceId());
            itemBuilder.service(service);
        }

        CartItem item = itemBuilder.build();
        cart.getItems().add(item);
        cartRepository.save(cart);
        return mapToResponse(cart);
    }

    @Override
    public CartResponse updateItemQuantity(Long userId, Long cartItemId, int quantity) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));
        if (!item.getCart().getId().equals(cart.getId()))
            throw new IllegalArgumentException("Cart item does not belong to this user's cart");
        if (quantity <= 0) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
        return mapToResponse(cartRepository.findById(cart.getId()).orElse(cart));
    }

    @Override
    public CartResponse removeItem(Long userId, Long cartItemId) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));
        if (!item.getCart().getId().equals(cart.getId()))
            throw new IllegalArgumentException("Cart item does not belong to this user's cart");
        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        return mapToResponse(cartRepository.findById(cart.getId()).orElse(cart));
    }

    @Override
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Override
    public double getCartTotal(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return cart.getItems().stream()
                .mapToDouble(item -> item.getUnitPrice() * item.getQuantity())
                .sum();
    }

    private CartItemResponse mapItemToResponse(CartItem item) {
        return CartItemResponse.builder()
                .id(item.getId())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getUnitPrice() * item.getQuantity())
                .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                .productName(item.getProduct() != null ? item.getProduct().getName() : null)
                .serviceId(item.getService() != null ? item.getService().getId() : null)
                .serviceName(item.getService() != null ? item.getService().getTitle() : null)
                .build();
    }

    private CartResponse mapToResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(this::mapItemToResponse)
                .collect(Collectors.toList());
        double total = items.stream().mapToDouble(i -> i.getUnitPrice() * i.getQuantity()).sum();
        return CartResponse.builder()
                .id(cart.getId())
                .createdAt(cart.getCreatedAt())
                .userId(cart.getUser().getId())
                .items(items)
                .total(total)
                .build();
    }
}