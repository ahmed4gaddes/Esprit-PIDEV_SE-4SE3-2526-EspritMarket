package tn.esprit.esprit_market.modules.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.order.dto.CreateDeliveryRequest;
import tn.esprit.esprit_market.modules.order.dto.DeliveryResponse;
import tn.esprit.esprit_market.modules.order.entity.Delivery;
import tn.esprit.esprit_market.modules.order.entity.Order;
import tn.esprit.esprit_market.modules.order.enums.DeliveryStatus;
import tn.esprit.esprit_market.modules.order.enums.OrderStatus;
import tn.esprit.esprit_market.modules.order.repository.DeliveryRepository;
import tn.esprit.esprit_market.modules.order.repository.OrderRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryServiceImpl implements IDeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;

    @Override
    public DeliveryResponse createDelivery(CreateDeliveryRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + request.getOrderId()));

        if (deliveryRepository.findByOrderId(request.getOrderId()).isPresent())
            throw new IllegalStateException("Delivery already exists for order: " + request.getOrderId());

        Delivery delivery = Delivery.builder()
                .order(order)
                .trackingNumber(UUID.randomUUID().toString().substring(0, 12).toUpperCase())
                .carrier(request.getCarrier())
                .status(DeliveryStatus.PENDING)
                .recipientName(request.getRecipientName())
                .recipientPhone(request.getRecipientPhone())
                .deliveryAddress(request.getDeliveryAddress())
                .deliveryNotes(request.getDeliveryNotes())
                .estimatedDeliveryDate(request.getEstimatedDeliveryDate())
                .build();

        Delivery saved = deliveryRepository.save(delivery);

        order.setStatus(OrderStatus.SHIPPED);
        orderRepository.save(order);

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryResponse getDeliveryByOrderId(Long orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found for order: " + orderId));
        return mapToResponse(delivery);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryResponse getDeliveryById(Long deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + deliveryId));
        return mapToResponse(delivery);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryResponse trackByTrackingNumber(String trackingNumber) {
        Delivery delivery = deliveryRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResourceNotFoundException("No delivery found with tracking number: " + trackingNumber));
        return mapToResponse(delivery);
    }

    @Override
    public DeliveryResponse updateDeliveryStatus(Long deliveryId, DeliveryStatus status) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + deliveryId));
        delivery.setStatus(status);

        if (status == DeliveryStatus.DELIVERED) {
            delivery.setActualDeliveryDate(new Date());
            delivery.getOrder().setStatus(OrderStatus.DELIVERED);
            orderRepository.save(delivery.getOrder());
        }

        return mapToResponse(deliveryRepository.save(delivery));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponse> getAllDeliveries() {
        return deliveryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponse> getDeliveriesByStatus(DeliveryStatus status) {
        return deliveryRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private DeliveryResponse mapToResponse(Delivery delivery) {
        return DeliveryResponse.builder()
                .id(delivery.getId())
                .trackingNumber(delivery.getTrackingNumber())
                .carrier(delivery.getCarrier())
                .status(delivery.getStatus())
                .estimatedDeliveryDate(delivery.getEstimatedDeliveryDate())
                .actualDeliveryDate(delivery.getActualDeliveryDate())
                .recipientName(delivery.getRecipientName())
                .recipientPhone(delivery.getRecipientPhone())
                .deliveryAddress(delivery.getDeliveryAddress())
                .deliveryNotes(delivery.getDeliveryNotes())
                .orderId(delivery.getOrder().getId())
                .build();
    }
}