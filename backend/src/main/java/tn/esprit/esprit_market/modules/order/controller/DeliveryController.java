package tn.esprit.esprit_market.modules.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.order.dto.CreateDeliveryRequest;
import tn.esprit.esprit_market.modules.order.dto.DeliveryResponse;
import tn.esprit.esprit_market.modules.order.enums.DeliveryStatus;
import tn.esprit.esprit_market.modules.order.service.IDeliveryService;
import tn.esprit.esprit_market.modules.order.service.IOrderService;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final IDeliveryService deliveryService;
    private final IOrderService orderService;
    private final UserRepository userRepository;

    private Long getLoggedInUserId(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getId();
    }

    @PostMapping
    public ResponseEntity<DeliveryResponse> createDelivery(
            @Valid @RequestBody CreateDeliveryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(deliveryService.createDelivery(request));
    }

    @GetMapping
    public ResponseEntity<List<DeliveryResponse>> getAllDeliveries() {
        return ResponseEntity.ok(deliveryService.getAllDeliveries());
    }

    @GetMapping("/{deliveryId}")
    public ResponseEntity<DeliveryResponse> getDeliveryById(@PathVariable Long deliveryId) {
        return ResponseEntity.ok(deliveryService.getDeliveryById(deliveryId));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<DeliveryResponse> getDeliveryByOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId) {
        Long userId = getLoggedInUserId(userDetails);
        boolean ownsOrder = orderService.getOrdersByUser(userId).stream()
                .anyMatch(o -> o.getId().equals(orderId));
        if (!ownsOrder) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(deliveryService.getDeliveryByOrderId(orderId));
    }

    @GetMapping("/track/{trackingNumber}")
    public ResponseEntity<DeliveryResponse> trackDelivery(@PathVariable String trackingNumber) {
        return ResponseEntity.ok(deliveryService.trackByTrackingNumber(trackingNumber));
    }

    @PatchMapping("/{deliveryId}/status")
    public ResponseEntity<DeliveryResponse> updateStatus(
            @PathVariable Long deliveryId,
            @RequestParam DeliveryStatus status) {
        return ResponseEntity.ok(deliveryService.updateDeliveryStatus(deliveryId, status));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<DeliveryResponse>> getDeliveriesByStatus(
            @PathVariable DeliveryStatus status) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByStatus(status));
    }
}