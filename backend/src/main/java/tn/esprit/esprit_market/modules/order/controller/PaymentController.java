package tn.esprit.esprit_market.modules.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.order.dto.PaymentResponse;
import tn.esprit.esprit_market.modules.order.enums.PaymentMethod;
import tn.esprit.esprit_market.modules.order.service.IOrderService;
import tn.esprit.esprit_market.modules.order.service.IPaymentService;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final IPaymentService paymentService;
    private final IOrderService orderService;
    private final UserRepository userRepository;

    private Long getLoggedInUserId(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getId();
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }

    @PostMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> processPayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId,
            @RequestParam PaymentMethod method) {
        Long userId = getLoggedInUserId(userDetails);
        boolean ownsOrder = orderService.getOrdersByUser(userId).stream()
                .anyMatch(o -> o.getId().equals(orderId));
        if (!ownsOrder) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(paymentService.processPayment(orderId, method));
    }
}