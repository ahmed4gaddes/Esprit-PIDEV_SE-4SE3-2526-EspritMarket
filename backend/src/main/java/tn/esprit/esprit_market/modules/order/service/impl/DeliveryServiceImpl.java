package tn.esprit.esprit_market.modules.order.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.order.dto.DeliveryRequestDTO;
import tn.esprit.esprit_market.modules.order.dto.DeliveryResponseDTO;
import tn.esprit.esprit_market.modules.order.entity.Delivery;
import tn.esprit.esprit_market.modules.order.entity.Order;
import tn.esprit.esprit_market.modules.order.enums.DeliveryStatus;
import tn.esprit.esprit_market.modules.order.enums.OrderStatus;
import tn.esprit.esprit_market.modules.order.mapper.OrderMapper;
import tn.esprit.esprit_market.modules.order.repository.DeliveryRepository;
import tn.esprit.esprit_market.modules.order.repository.OrderRepository;
import tn.esprit.esprit_market.modules.order.service.IDeliveryService;

import java.util.Calendar;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements IDeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public DeliveryResponseDTO initiateDelivery(Long orderId, DeliveryRequestDTO request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        if (deliveryRepository.findByOrderId(orderId).isPresent()) {
            throw new IllegalArgumentException("Delivery already initiated for this order");
        }

        // Estimate delivery date 3 days from now
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 3);
        Date estimatedDate = cal.getTime();

        Delivery delivery = Delivery.builder()
                .order(order)
                .status(DeliveryStatus.PENDING)
                .deliveryAddress(request.getDeliveryAddress())
                .recipientName(request.getRecipientName() != null ? request.getRecipientName() : order.getUser().getName())
                .recipientPhone(request.getRecipientPhone())
                .deliveryNotes(request.getDeliveryNotes())
                .estimatedDeliveryDate(estimatedDate)
                .build();

        Delivery savedDelivery = deliveryRepository.save(delivery);
        
        order.setDelivery(savedDelivery);
        orderRepository.save(order);

        return orderMapper.toDeliveryResponseDTO(savedDelivery);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryResponseDTO getDeliveryByOrderId(Long orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found for order: " + orderId));
        return orderMapper.toDeliveryResponseDTO(delivery);
    }

    @Override
    @Transactional
    public DeliveryResponseDTO updateDeliveryStatus(Long orderId, DeliveryStatus status, String trackingNumber, String carrier) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found for order: " + orderId));
                
        delivery.setStatus(status);
        if (trackingNumber != null) {
            delivery.setTrackingNumber(trackingNumber);
        }
        if (carrier != null) {
            delivery.setCarrier(carrier);
        }
        
        if (status == DeliveryStatus.DELIVERED) {
            delivery.setActualDeliveryDate(new Date());
            
            Order order = delivery.getOrder();
            order.setStatus(OrderStatus.DELIVERED);
            orderRepository.save(order);
        } else if (status == DeliveryStatus.SHIPPED) {
            Order order = delivery.getOrder();
            order.setStatus(OrderStatus.SHIPPED);
            orderRepository.save(order);
        }

        return orderMapper.toDeliveryResponseDTO(deliveryRepository.save(delivery));
    }
}
