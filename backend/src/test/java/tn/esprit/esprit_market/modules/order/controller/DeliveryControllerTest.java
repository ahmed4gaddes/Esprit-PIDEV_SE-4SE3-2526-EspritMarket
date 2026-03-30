package tn.esprit.esprit_market.modules.order.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tn.esprit.esprit_market.modules.order.dto.DeliveryRequestDTO;
import tn.esprit.esprit_market.modules.order.dto.DeliveryResponseDTO;
import tn.esprit.esprit_market.modules.order.enums.DeliveryStatus;
import tn.esprit.esprit_market.modules.order.service.IDeliveryService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryControllerTest {

    @Mock
    private IDeliveryService deliveryService;

    @InjectMocks
    private DeliveryController controller;

    private DeliveryResponseDTO deliveryResponse;

    @BeforeEach
    void setUp() {
        deliveryResponse = DeliveryResponseDTO.builder()
                .id(1L)
                .orderId(1L)
                .status(DeliveryStatus.PENDING)
                .deliveryAddress("123 Rue ESPRIT")
                .build();
    }

    @Test
    void testInitiateDelivery() {
        DeliveryRequestDTO request = DeliveryRequestDTO.builder()
                .deliveryAddress("123 Rue ESPRIT")
                .recipientName("Ahmed")
                .build();
        when(deliveryService.initiateDelivery(eq(1L), any(DeliveryRequestDTO.class))).thenReturn(deliveryResponse);

        ResponseEntity<DeliveryResponseDTO> res = controller.initiateDelivery(1L, request);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNotNull(res.getBody());
    }

    @Test
    void testGetDeliveryByOrderId() {
        when(deliveryService.getDeliveryByOrderId(1L)).thenReturn(deliveryResponse);

        ResponseEntity<DeliveryResponseDTO> res = controller.getDeliveryByOrderId(1L);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(1L, res.getBody().getOrderId());
    }

    @Test
    void testUpdateDeliveryStatus() {
        when(deliveryService.updateDeliveryStatus(eq(1L), eq(DeliveryStatus.IN_TRANSIT), anyString(), anyString()))
                .thenReturn(deliveryResponse);

        ResponseEntity<DeliveryResponseDTO> res = controller.updateDeliveryStatus(
                1L, DeliveryStatus.IN_TRANSIT, "TRK-123", "DHL");

        assertEquals(HttpStatus.OK, res.getStatusCode());
    }
}
