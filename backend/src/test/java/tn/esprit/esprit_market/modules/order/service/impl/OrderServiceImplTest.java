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
import tn.esprit.esprit_market.modules.order.entity.OrderItem;
import tn.esprit.esprit_market.modules.order.enums.OrderStatus;
import tn.esprit.esprit_market.modules.order.mapper.OrderMapper;
import tn.esprit.esprit_market.modules.order.repository.CartRepository;
import tn.esprit.esprit_market.modules.order.repository.OrderItemRepository;
import tn.esprit.esprit_market.modules.order.repository.OrderRepository;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.store.entity.StockMovement;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryProduct;
import tn.esprit.esprit_market.modules.store.service.IStockMovement;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.enums.Role;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

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
    private Order order;
    private Product product;
    private OrderResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(10L);
        user.setEmail("user@mail.com");
        user.setRole(Role.CUSTOMER);

        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setStock(50);

        CartItem item = new CartItem();
        item.setId(100L);
        item.setProduct(product);
        item.setQuantity(2);
        item.setUnitPrice(1000.0);

        cart = new Cart();
        cart.setId(5L);
        cart.setUser(user);
        cart.setItems(Arrays.asList(item));

        order = new Order();
        order.setId(20L);
        order.setUser(user);
        order.setStatus(OrderStatus.CREATED);
        order.setTotal(2007.0);

        responseDTO = new OrderResponseDTO();
        responseDTO.setId(20L);
        responseDTO.setTotalAmount(2007.0);
        responseDTO.setStatus(OrderStatus.CREATED);
    }

    @Test
    void testCreateOrderFromCart() {
        OrderRequestDTO req = new OrderRequestDTO();
        req.setShippingAddress("123 Street");

        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(10L)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(productRepository.save(product)).thenReturn(product);
        when(stockMovementService.addStock(any(StockMovement.class))).thenReturn(new StockMovement());
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(new OrderItem());
        doNothing().when(cartRepository).delete(cart);
        when(orderMapper.toOrderResponseDTO(order)).thenReturn(responseDTO);

        OrderResponseDTO result = orderService.createOrderFromCart("user@mail.com", req);

        assertNotNull(result);
        assertEquals(2007.0, result.getTotalAmount());
        assertEquals(48, product.getStock()); // 50 - 2
        verify(stockMovementService).addStock(any(StockMovement.class));
        verify(cartRepository).delete(cart);
    }

    @Test
    void testCreateOrderFromCartThrowsExceptionForInsufficientStock() {
        OrderRequestDTO req = new OrderRequestDTO();
        product.setStock(1); // Requesting 2

        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(10L)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrderFromCart("user@mail.com", req));
    }

    @Test
    void testGetOrderById() {
        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(orderRepository.findById(20L)).thenReturn(Optional.of(order));
        when(orderMapper.toOrderResponseDTO(order)).thenReturn(responseDTO);

        OrderResponseDTO result = orderService.getOrderById(20L, "user@mail.com");

        assertNotNull(result);
        assertEquals(20L, result.getId());
    }

    @Test
    void testGetOrderByIdUnauthorized() {
        User otherUser = new User();
        otherUser.setId(99L);
        otherUser.setEmail("other@mail.com");
        otherUser.setRole(Role.CUSTOMER);

        when(userRepository.findByEmail("other@mail.com")).thenReturn(Optional.of(otherUser));
        when(orderRepository.findById(20L)).thenReturn(Optional.of(order));

        assertThrows(IllegalArgumentException.class, () -> orderService.getOrderById(20L, "other@mail.com"));
    }

    @Test
    void testGetUserOrders() {
        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(orderRepository.findByUserIdOrderByOrderDateDesc(10L)).thenReturn(Arrays.asList(order));
        when(orderMapper.toOrderResponseDTO(order)).thenReturn(responseDTO);

        List<OrderResponseDTO> results = orderService.getUserOrders("user@mail.com");

        assertEquals(1, results.size());
    }

    @Test
    void testGetAllOrders() {
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order));
        when(orderMapper.toOrderResponseDTO(order)).thenReturn(responseDTO);

        List<OrderResponseDTO> results = orderService.getAllOrders();

        assertEquals(1, results.size());
    }

    @Test
    void testUpdateOrderStatus() {
        when(orderRepository.findById(20L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toOrderResponseDTO(order)).thenReturn(responseDTO);

        OrderResponseDTO result = orderService.updateOrderStatus(20L, OrderStatus.PAID);

        assertNotNull(result);
        assertEquals(OrderStatus.PAID, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void testCancelOrder() {
        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(orderRepository.findById(20L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        orderService.cancelOrder(20L, "user@mail.com");

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void testCancelOrderThrowsExceptionWhenShipped() {
        order.setStatus(OrderStatus.SHIPPED);
        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(orderRepository.findById(20L)).thenReturn(Optional.of(order));

        assertThrows(IllegalArgumentException.class, () -> orderService.cancelOrder(20L, "user@mail.com"));
    }
}
