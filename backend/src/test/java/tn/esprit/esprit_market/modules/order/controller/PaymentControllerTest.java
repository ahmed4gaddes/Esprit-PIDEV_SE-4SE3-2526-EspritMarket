package tn.esprit.esprit_market.modules.order.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tn.esprit.esprit_market.modules.order.dto.PaymentRequestDTO;
import tn.esprit.esprit_market.modules.order.dto.PaymentResponseDTO;
import tn.esprit.esprit_market.modules.order.service.IPaymentService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private IPaymentService paymentService;

    @InjectMocks
    private PaymentController controller;

    private PaymentResponseDTO paymentResponse;

    @BeforeEach
    void setUp() {
        paymentResponse = PaymentResponseDTO.builder()
                .id(1L)
                .orderId(1L)
                .build();
    }

    @Test
    void testProcessPayment() {
        PaymentRequestDTO request = PaymentRequestDTO.builder()
                .build();
        when(paymentService.processPayment(eq(1L), any(PaymentRequestDTO.class))).thenReturn(paymentResponse);

        ResponseEntity<PaymentResponseDTO> res = controller.processPayment(1L, request);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNotNull(res.getBody());
    }

    @Test
    void testGetPaymentByOrderId() {
        when(paymentService.getPaymentByOrderId(1L)).thenReturn(paymentResponse);

        ResponseEntity<PaymentResponseDTO> res = controller.getPaymentByOrderId(1L);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(1L, res.getBody().getOrderId());
    }
}
