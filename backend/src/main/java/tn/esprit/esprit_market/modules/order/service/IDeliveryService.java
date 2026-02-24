package tn.esprit.esprit_market.modules.order.service;

import tn.esprit.esprit_market.modules.order.dto.CreateDeliveryRequest;
import tn.esprit.esprit_market.modules.order.dto.DeliveryResponse;
import tn.esprit.esprit_market.modules.order.enums.DeliveryStatus;

import java.util.List;

public interface IDeliveryService {
    DeliveryResponse createDelivery(CreateDeliveryRequest request);
    DeliveryResponse getDeliveryByOrderId(Long orderId);
    DeliveryResponse getDeliveryById(Long deliveryId);
    DeliveryResponse trackByTrackingNumber(String trackingNumber);
    DeliveryResponse updateDeliveryStatus(Long deliveryId, DeliveryStatus status);
    List<DeliveryResponse> getAllDeliveries();
    List<DeliveryResponse> getDeliveriesByStatus(DeliveryStatus status);
}