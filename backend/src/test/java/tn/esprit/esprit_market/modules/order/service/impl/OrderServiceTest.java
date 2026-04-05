package tn.esprit.esprit_market.modules.order.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.modules.order.dto.OrderRequestDTO;
import tn.esprit.esprit_market.modules.order.dto.OrderResponseDTO;
import tn.esprit.esprit_market.modules.order.entity.Cart;
import tn.esprit.esprit_market.modules.order.entity.CartItem;
import tn.esprit.esprit_market.modules.order.entity.Order;
import tn.esprit.esprit_market.modules.order.enums.OrderStatus;
import tn.esprit.esprit_market.modules.order.mapper.OrderMapper;
import tn.esprit.esprit_market.modules.order.repository.CartRepository;
import tn.esprit.esprit_market.modules.order.repository.OrderItemRepository;
import tn.esprit.esprit_market.modules.order.repository.OrderRepository;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryProduct;
import tn.esprit.esprit_market.modules.store.service.IStockMovement;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private IStockMovement stockMovementService;
    @Mock
    private IRepositoryProduct productRepository;
    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Cart cart;
    private Product product;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@esprit.tn");

        product = new Product();
        product.setId(10L);
        product.setName("Product Name");
        product.setStock(100);
        product.setPrice(10.0);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(5);
        cartItem.setUnitPrice(10.0);

        cart = Cart.builder()
                .id(1L)
                .user(user)
                .items(new ArrayList<>(List.of(cartItem)))
                .build();
    }

    @Test
    void createOrderFromCart_ShouldDecrementStockAndClearCart() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);
        when(orderMapper.toOrderResponseDTO(any())).thenReturn(new OrderResponseDTO());

        OrderRequestDTO request = new OrderRequestDTO();
        request.setShippingAddress("123 Esprit St");

        OrderResponseDTO result = orderService.createOrderFromCart("test@esprit.tn", request);

        assertNotNull(result);
        assertEquals(95, product.getStock()); // 100 - 5
        verify(productRepository).save(product);
        verify(stockMovementService).addStock(any(tn.esprit.esprit_market.modules.store.entity.StockMovement.class));
        verify(cartRepository).delete(cart);
    }

    @Test
    void createOrderFromCart_InsufficientStock_ShouldThrowException() {
        product.setStock(2); // Only 2 left, but cart has 5
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        OrderRequestDTO request = new OrderRequestDTO();
        request.setShippingAddress("123 Esprit St");

        assertThrows(IllegalArgumentException.class, () -> 
            orderService.createOrderFromCart("test@esprit.tn", request)
        );
    }
}
