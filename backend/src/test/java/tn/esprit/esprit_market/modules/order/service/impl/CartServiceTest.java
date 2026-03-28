package tn.esprit.esprit_market.modules.order.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.order.dto.CartItemRequestDTO;
import tn.esprit.esprit_market.modules.order.dto.CartResponseDTO;
import tn.esprit.esprit_market.modules.order.entity.Cart;
import tn.esprit.esprit_market.modules.order.entity.CartItem;
import tn.esprit.esprit_market.modules.order.mapper.OrderMapper;
import tn.esprit.esprit_market.modules.order.repository.CartItemRepository;
import tn.esprit.esprit_market.modules.order.repository.CartRepository;
import tn.esprit.esprit_market.modules.service.entity.Service;
import tn.esprit.esprit_market.modules.service.repository.ServiceRepository;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryProduct;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private IRepositoryProduct productRepository;
    @Mock
    private ServiceRepository serviceRepository;
    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    private User user;
    private Cart cart;
    private Product product;
    private Service service;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@esprit.tn");

        cart = Cart.builder()
                .id(1L)
                .user(user)
                .items(new ArrayList<>())
                .build();

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(100.0);
        product.setStock(10);

        service = new Service();
        service.setId(1L);
        service.setTitle("Test Service");
        service.setPrice(50.0);
    }

    @Test
    void getCart_ShouldReturnCartDTO() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.of(cart));
        when(orderMapper.toCartResponseDTO(any())).thenReturn(new CartResponseDTO());

        CartResponseDTO result = cartService.getCart("test@esprit.tn");

        assertNotNull(result);
        verify(cartRepository).findByUserId(1L);
    }

    @Test
    void addItemToCart_Product_ShouldAddOrUpdateItem() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.of(cart));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cart));
        when(orderMapper.toCartResponseDTO(any())).thenReturn(new CartResponseDTO());

        CartItemRequestDTO request = new CartItemRequestDTO();
        request.setProductId(1L);
        request.setQuantity(2);

        cartService.addItemToCart("test@esprit.tn", request);

        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void addItemToCart_Service_ShouldAddOrUpdateItem() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.of(cart));
        when(serviceRepository.findById(anyLong())).thenReturn(Optional.of(service));
        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cart));
        when(orderMapper.toCartResponseDTO(any())).thenReturn(new CartResponseDTO());

        CartItemRequestDTO request = new CartItemRequestDTO();
        request.setServiceId(1L);
        request.setQuantity(1);

        cartService.addItemToCart("test@esprit.tn", request);

        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void clearCart_ShouldRemoveAllItems() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.of(cart));
        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cart));
        when(orderMapper.toCartResponseDTO(any())).thenReturn(new CartResponseDTO());

        cartService.clearCart("test@esprit.tn");

        verify(cartItemRepository).deleteAll(any());
        assertTrue(cart.getItems().isEmpty());
    }
}
