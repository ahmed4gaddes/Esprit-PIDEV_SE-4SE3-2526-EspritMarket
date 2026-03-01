package tn.esprit.esprit_market.modules.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.order.dto.CreateDeliveryRequest;
import tn.esprit.esprit_market.modules.order.dto.DeliveryResponse;
import tn.esprit.esprit_market.modules.order.entity.Delivery;
import tn.esprit.esprit_market.modules.order.entity.Order;
import tn.esprit.esprit_market.modules.order.enums.DeliveryStatus;
import tn.esprit.esprit_market.modules.order.enums.OrderStatus;
import tn.esprit.esprit_market.modules.order.repository.DeliveryRepository;
import tn.esprit.esprit_market.modules.order.repository.OrderRepository;
import tn.esprit.esprit_market.modules.order.service.DeliveryServiceImpl;
import tn.esprit.esprit_market.modules.user.entity.User;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceImplTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private DeliveryServiceImpl deliveryService;

    private User user;
    private Order order;
    private Delivery delivery;

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
                .status(OrderStatus.PAID)
                .deliveryAddress("123 Test Street")
                .items(new ArrayList<>())
                .build();

        delivery = Delivery.builder()
                .id(1L)
                .order(order)
                .trackingNumber("TRK-ABC123")
                .carrier("DHL")
                .status(DeliveryStatus.PENDING)
                .recipientName("Test User")
                .recipientPhone("12345678")
                .deliveryAddress("123 Test Street")
                .build();
    }

    @Test
    void getDeliveryById_existingDelivery_returnsDelivery() {
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));

        DeliveryResponse response = deliveryService.getDeliveryById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("DHL", response.getCarrier());
        assertEquals(DeliveryStatus.PENDING, response.getStatus());
    }

    @Test
    void getDeliveryById_notFound_throwsException() {
        when(deliveryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> deliveryService.getDeliveryById(99L));
    }

    @Test
    void trackByTrackingNumber_validNumber_returnsDelivery() {
        when(deliveryRepository.findByTrackingNumber("TRK-ABC123"))
                .thenReturn(Optional.of(delivery));

        DeliveryResponse response = deliveryService.trackByTrackingNumber("TRK-ABC123");

        assertNotNull(response);
        assertEquals("TRK-ABC123", response.getTrackingNumber());
    }

    @Test
    void trackByTrackingNumber_notFound_throwsException() {
        when(deliveryRepository.findByTrackingNumber("INVALID"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> deliveryService.trackByTrackingNumber("INVALID"));
    }

    @Test
    void updateDeliveryStatus_toDelivered_setsActualDate() {
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        DeliveryResponse response = deliveryService.updateDeliveryStatus(1L, DeliveryStatus.DELIVERED);

        assertNotNull(response);
        assertEquals(DeliveryStatus.DELIVERED, delivery.getStatus());
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
        assertNotNull(delivery.getActualDeliveryDate());
    }

    @Test
    void createDelivery_duplicateDelivery_throwsException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(deliveryRepository.findByOrderId(1L)).thenReturn(Optional.of(delivery));

        CreateDeliveryRequest request = new CreateDeliveryRequest();
        request.setOrderId(1L);
        request.setCarrier("DHL");
        request.setRecipientName("Test User");
        request.setRecipientPhone("12345678");
        request.setDeliveryAddress("123 Test Street");

        assertThrows(IllegalStateException.class,
                () -> deliveryService.createDelivery(request));
    }
}