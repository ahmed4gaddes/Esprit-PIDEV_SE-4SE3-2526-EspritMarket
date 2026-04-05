package tn.esprit.esprit_market.modules.order.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tn.esprit.esprit_market.modules.order.dto.*;
import tn.esprit.esprit_market.modules.order.entity.*;
import tn.esprit.esprit_market.modules.order.enums.DeliveryStatus;
import tn.esprit.esprit_market.modules.order.enums.OrderStatus;
import tn.esprit.esprit_market.modules.order.enums.PaymentMethod;
import tn.esprit.esprit_market.modules.service.entity.Service;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.user.entity.User;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {

    private OrderMapper orderMapper;

    @BeforeEach
    void setUp() {
        orderMapper = new OrderMapper();
    }

    @Test
    void testToCartItemResponseDTO() {
        Product p = new Product();
        p.setId(10L);
        p.setName("Test Product");

        CartItem item = new CartItem();
        item.setId(1L);
        item.setQuantity(2);
        item.setUnitPrice(50.0);
        item.setProduct(p);

        CartItemResponseDTO dto = orderMapper.toCartItemResponseDTO(item);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(2, dto.getQuantity());
        assertEquals(50.0, dto.getUnitPrice());
        assertEquals(100.0, dto.getTotalPrice());
        assertEquals(10L, dto.getProductId());
        assertEquals("Test Product", dto.getProductName());
    }

    @Test
    void testToCartResponseDTO() {
        User user = new User();
        user.setId(5L);

        Cart cart = new Cart();
        cart.setId(2L);
        cart.setUser(user);
        cart.setCreatedAt(new Date());

        CartItem item = new CartItem();
        item.setQuantity(1);
        item.setUnitPrice(100.0);
        cart.setItems(List.of(item));

        CartResponseDTO dto = orderMapper.toCartResponseDTO(cart);

        assertNotNull(dto);
        assertEquals(2L, dto.getId());
        assertEquals(5L, dto.getUserId());
        assertEquals(100.0, dto.getSubtotal());
        assertEquals(7.0, dto.getDeliveryFee());
        assertEquals(107.0, dto.getTotal());
        assertEquals(1, dto.getItems().size());
    }

    @Test
    void testToOrderItemResponseDTO() {
        Service s = new Service();
        s.setId(20L);
        s.setTitle("Test Service");

        OrderItem item = new OrderItem();
        item.setId(5L);
        item.setQuantity(1);
        item.setUnitPrice(250.0);
        item.setService(s);

        OrderItemResponseDTO dto = orderMapper.toOrderItemResponseDTO(item);

        assertNotNull(dto);
        assertEquals(250.0, dto.getTotalPrice());
        assertEquals(20L, dto.getServiceId());
        assertEquals("Test Service", dto.getServiceName());
    }

    @Test
    void testToOrderResponseDTO() {
        User user = new User();
        user.setId(8L);
        user.setName("John Doe");

        Order order = new Order();
        order.setId(3L);
        order.setStatus(OrderStatus.CREATED);
        order.setTotal(300.0);
        order.setUser(user);

        OrderItem item = new OrderItem();
        item.setQuantity(2);
        item.setUnitPrice(150.0);
        order.setItems(List.of(item));

        OrderResponseDTO dto = orderMapper.toOrderResponseDTO(order);

        assertNotNull(dto);
        assertEquals(3L, dto.getId());
        assertEquals(OrderStatus.CREATED, dto.getStatus());
        assertEquals(300.0, dto.getTotalAmount());
        assertEquals(8L, dto.getUserId());
        assertEquals("John Doe", dto.getUserName());
        assertEquals(1, dto.getItems().size());
    }

    @Test
    void testToPaymentResponseDTO() {
        Order order = new Order();
        order.setId(3L);

        Payment payment = new Payment();
        payment.setId(9L);
        payment.setAmount(100.0);
        payment.setMethod(PaymentMethod.CARD);
        payment.setTransactionRef("TXN123");
        payment.setOrder(order);

        PaymentResponseDTO dto = orderMapper.toPaymentResponseDTO(payment);

        assertNotNull(dto);
        assertEquals(9L, dto.getId());
        assertEquals(100.0, dto.getAmount());
        assertEquals(PaymentMethod.CARD, dto.getMethod());
        assertEquals("TXN123", dto.getTransactionRef());
        assertEquals(3L, dto.getOrderId());
    }

    @Test
    void testToDeliveryResponseDTO() {
        Order order = new Order();
        order.setId(3L);

        Delivery delivery = new Delivery();
        delivery.setId(15L);
        delivery.setTrackingNumber("TRK999");
        delivery.setCarrier("DHL");
        delivery.setStatus(DeliveryStatus.SHIPPED);
        delivery.setOrder(order);

        DeliveryResponseDTO dto = orderMapper.toDeliveryResponseDTO(delivery);

        assertNotNull(dto);
        assertEquals(15L, dto.getId());
        assertEquals("TRK999", dto.getTrackingNumber());
        assertEquals("DHL", dto.getCarrier());
        assertEquals(DeliveryStatus.SHIPPED, dto.getStatus());
        assertEquals(3L, dto.getOrderId());
    }
    
    @Test
    void testNullInputs() {
        assertNull(orderMapper.toCartItemResponseDTO(null));
        assertNull(orderMapper.toCartResponseDTO(null));
        assertNull(orderMapper.toOrderItemResponseDTO(null));
        assertNull(orderMapper.toOrderResponseDTO(null));
        assertNull(orderMapper.toPaymentResponseDTO(null));
        assertNull(orderMapper.toDeliveryResponseDTO(null));
    }
}
