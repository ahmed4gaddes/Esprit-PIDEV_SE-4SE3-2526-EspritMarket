package tn.esprit.esprit_market.modules.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.order.dto.DeliveryRequestDTO;
import tn.esprit.esprit_market.modules.order.dto.DeliveryResponseDTO;
import tn.esprit.esprit_market.modules.order.enums.DeliveryStatus;
import tn.esprit.esprit_market.modules.order.service.IDeliveryService;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final IDeliveryService deliveryService;

    @PostMapping("/order/{orderId}")
    public ResponseEntity<DeliveryResponseDTO> initiateDelivery(
            @PathVariable Long orderId,
            @Valid @RequestBody DeliveryRequestDTO request) {
        return ResponseEntity.ok(deliveryService.initiateDelivery(orderId, request));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<DeliveryResponseDTO> getDeliveryByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(deliveryService.getDeliveryByOrderId(orderId));
    }

    @PutMapping("/order/{orderId}/status")
    public ResponseEntity<DeliveryResponseDTO> updateDeliveryStatus(
            @PathVariable Long orderId,
            @RequestParam DeliveryStatus status,
            @RequestParam(required = false) String trackingNumber,
            @RequestParam(required = false) String carrier) {
        return ResponseEntity.ok(deliveryService.updateDeliveryStatus(orderId, status, trackingNumber, carrier));
    }
}
