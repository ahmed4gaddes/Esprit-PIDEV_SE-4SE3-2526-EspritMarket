package tn.esprit.esprit_market.modules.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.order.dto.PaymentResponse;
import tn.esprit.esprit_market.modules.order.entity.Order;
import tn.esprit.esprit_market.modules.order.entity.Payment;
import tn.esprit.esprit_market.modules.order.enums.OrderStatus;
import tn.esprit.esprit_market.modules.order.enums.PaymentMethod;
import tn.esprit.esprit_market.modules.order.repository.OrderRepository;
import tn.esprit.esprit_market.modules.order.repository.PaymentRepository;
import tn.esprit.esprit_market.modules.order.service.PaymentServiceImpl;
import tn.esprit.esprit_market.modules.user.entity.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private User user;
    private Order order;
    private Payment payment;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@gmail.com")
                .password("password")
                .build();

        order = Order.builder()
                .id(1L)
                .user(user)
                .total(100.0)
                .status(OrderStatus.CREATED)
                .deliveryAddress("123 Test Street")
                .items(new ArrayList<>())
                .build();

        payment = Payment.builder()
                .id(1L)
                .order(order)
                .amount(100.0)
                .method(PaymentMethod.CARD)
                .paymentDate(new Date())
                .transactionRef("TXN-123")
                .build();
    }

    @Test
    void getPaymentById_existingPayment_returnsPayment() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        PaymentResponse response = paymentService.getPaymentById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(100.0, response.getAmount());
        assertEquals(PaymentMethod.CARD, response.getMethod());
    }

    @Test
    void getPaymentById_notFound_throwsException() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> paymentService.getPaymentById(99L));
    }

    @Test
    void getPaymentByOrderId_existingPayment_returnsPayment() {
        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.of(payment));

        PaymentResponse response = paymentService.getPaymentByOrderId(1L);

        assertNotNull(response);
        assertEquals(1L, response.getOrderId());
    }

    @Test
    void processPayment_validOrder_createsPayment() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        PaymentResponse response = paymentService.processPayment(1L, PaymentMethod.CARD);

        assertNotNull(response);
        assertEquals(PaymentMethod.CARD, response.getMethod());
        verify(paymentRepository, times(1)).save(any(Payment.class));
        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    void processPayment_alreadyPaid_throwsException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.of(payment));

        assertThrows(IllegalStateException.class,
                () -> paymentService.processPayment(1L, PaymentMethod.CARD));
    }

    @Test
    void processPayment_orderNotFound_throwsException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> paymentService.processPayment(99L, PaymentMethod.CARD));
    }
}