package tn.esprit.esprit_market.modules.order.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.order.dto.CartItemRequestDTO;
import tn.esprit.esprit_market.modules.order.dto.CartResponseDTO;
import tn.esprit.esprit_market.modules.order.entity.Cart;
import tn.esprit.esprit_market.modules.order.entity.CartItem;
import tn.esprit.esprit_market.modules.order.mapper.OrderMapper;
import tn.esprit.esprit_market.modules.order.repository.CartItemRepository;
import tn.esprit.esprit_market.modules.order.repository.CartRepository;
import tn.esprit.esprit_market.modules.order.service.ICartService;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryProduct;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements ICartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final IRepositoryProduct productRepository;
    private final tn.esprit.esprit_market.modules.service.repository.ServiceRepository serviceRepository;
    private final OrderMapper orderMapper;

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = Cart.builder().user(user).build();
                    return cartRepository.save(newCart);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponseDTO getCart(String userEmail) {
        User user = getUser(userEmail);
        Cart cart = getOrCreateCart(user);
        return orderMapper.toCartResponseDTO(cart);
    }

    @Override
    @Transactional
    public CartResponseDTO addItemToCart(String userEmail, CartItemRequestDTO request) {
        User user = getUser(userEmail);
        Cart cart = getOrCreateCart(user);

        if (request.getProductId() != null) {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + request.getProductId()));
            
            CartItem existingItem = cart.getItems().stream()
                    .filter(i -> i.getProduct() != null && i.getProduct().getId().equals(product.getId()))
                    .findFirst().orElse(null);
                    
            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            } else {
                CartItem newItem = CartItem.builder()
                        .cart(cart)
                        .product(product)
                        .quantity(request.getQuantity())
                        .unitPrice(product.getPrice())
                        .build();
                cart.getItems().add(newItem);
            }
        } else if (request.getServiceId() != null) {
            tn.esprit.esprit_market.modules.service.entity.Service service = serviceRepository.findById(request.getServiceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Service not found: " + request.getServiceId()));
            
            CartItem existingItem = cart.getItems().stream()
                    .filter(i -> i.getService() != null && i.getService().getId().equals(service.getId()))
                    .findFirst().orElse(null);
                    
            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            } else {
                CartItem newItem = CartItem.builder()
                        .cart(cart)
                        .service(service)
                        .quantity(request.getQuantity())
                        .unitPrice(service.getPrice())
                        .build();
                cart.getItems().add(newItem);
            }
        } else {
            throw new IllegalArgumentException("Must provide either productId or serviceId");
        }

        Cart savedCart = cartRepository.save(cart);
        return orderMapper.toCartResponseDTO(savedCart);
    }

    @Override
    @Transactional
    public CartResponseDTO updateItemQuantity(String userEmail, Long cartItemId, int quantity) {
        User user = getUser(userEmail);
        Cart cart = getOrCreateCart(user);
        
        CartItem item = cartItemRepository.findById(cartItemId)
                .filter(i -> i.getCart().getId().equals(cart.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("CartItem not found in your cart"));
                
        if (quantity <= 0) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
        
        return orderMapper.toCartResponseDTO(cart);
    }

    @Override
    @Transactional
    public CartResponseDTO removeItemFromCart(String userEmail, Long cartItemId) {
        return updateItemQuantity(userEmail, cartItemId, 0);
    }

    @Override
    @Transactional
    public CartResponseDTO clearCart(String userEmail) {
        User user = getUser(userEmail);
        Cart cart = getOrCreateCart(user);
        
        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        
        return orderMapper.toCartResponseDTO(cart);
    }
}
