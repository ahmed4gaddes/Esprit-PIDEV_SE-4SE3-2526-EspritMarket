package tn.esprit.esprit_market.modules.order.service;

import tn.esprit.esprit_market.modules.order.dto.DeliveryRequestDTO;
import tn.esprit.esprit_market.modules.order.dto.DeliveryResponseDTO;
import tn.esprit.esprit_market.modules.order.enums.DeliveryStatus;

public interface IDeliveryService {
    DeliveryResponseDTO initiateDelivery(Long orderId, DeliveryRequestDTO request);
    DeliveryResponseDTO getDeliveryByOrderId(Long orderId);
    DeliveryResponseDTO updateDeliveryStatus(Long orderId, DeliveryStatus status, String trackingNumber, String carrier);
}
