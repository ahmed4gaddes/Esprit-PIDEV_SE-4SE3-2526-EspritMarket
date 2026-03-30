package tn.esprit.esprit_market.modules.order.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import tn.esprit.esprit_market.modules.order.dto.OrderRequestDTO;
import tn.esprit.esprit_market.modules.order.dto.OrderResponseDTO;
import tn.esprit.esprit_market.modules.order.enums.OrderStatus;
import tn.esprit.esprit_market.modules.order.service.IOrderService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private IOrderService orderService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private OrderController controller;

    private OrderRequestDTO requestDto;
    private OrderResponseDTO responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new OrderRequestDTO();
        
        responseDto = new OrderResponseDTO();
        responseDto.setId(5L);
        responseDto.setStatus(OrderStatus.CREATED);
        responseDto.setTotalAmount(150.0);
    }

    @Test
    void testCreateOrder() {
        when(authentication.getName()).thenReturn("user@mail.com");
        when(orderService.createOrderFromCart(eq("user@mail.com"), any(OrderRequestDTO.class))).thenReturn(responseDto);

        ResponseEntity<OrderResponseDTO> response = controller.createOrder(requestDto, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5L, response.getBody().getId());
        verify(orderService).createOrderFromCart(eq("user@mail.com"), any(OrderRequestDTO.class));
    }

    @Test
    void testGetMyOrders() {
        when(authentication.getName()).thenReturn("user@mail.com");
        when(orderService.getUserOrders("user@mail.com")).thenReturn(Arrays.asList(responseDto));

        ResponseEntity<List<OrderResponseDTO>> response = controller.getMyOrders(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(orderService).getUserOrders("user@mail.com");
    }

    @Test
    void testGetOrderById() {
        when(authentication.getName()).thenReturn("user@mail.com");
        when(orderService.getOrderById(5L, "user@mail.com")).thenReturn(responseDto);

        ResponseEntity<OrderResponseDTO> response = controller.getOrderById(5L, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(5L, response.getBody().getId());
        verify(orderService).getOrderById(5L, "user@mail.com");
    }

    @Test
    void testGetAllOrders() {
        when(orderService.getAllOrders()).thenReturn(Arrays.asList(responseDto));

        ResponseEntity<List<OrderResponseDTO>> response = controller.getAllOrders();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(orderService).getAllOrders();
    }

    @Test
    void testUpdateOrderStatus() {
        responseDto.setStatus(OrderStatus.SHIPPED);
        when(orderService.updateOrderStatus(5L, OrderStatus.SHIPPED)).thenReturn(responseDto);

        ResponseEntity<OrderResponseDTO> response = controller.updateOrderStatus(5L, OrderStatus.SHIPPED);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(OrderStatus.SHIPPED, response.getBody().getStatus());
        verify(orderService).updateOrderStatus(5L, OrderStatus.SHIPPED);
    }

    @Test
    void testCancelOrder() {
        when(authentication.getName()).thenReturn("user@mail.com");
        doNothing().when(orderService).cancelOrder(5L, "user@mail.com");

        ResponseEntity<Void> response = controller.cancelOrder(5L, authentication);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(orderService).cancelOrder(5L, "user@mail.com");
    }
}
