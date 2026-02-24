package tn.esprit.esprit_market.modules.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.order.dto.PaymentResponse;
import tn.esprit.esprit_market.modules.order.entity.Order;
import tn.esprit.esprit_market.modules.order.entity.Payment;
import tn.esprit.esprit_market.modules.order.enums.OrderStatus;
import tn.esprit.esprit_market.modules.order.enums.PaymentMethod;
import tn.esprit.esprit_market.modules.order.repository.OrderRepository;
import tn.esprit.esprit_market.modules.order.repository.PaymentRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements IPaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order: " + orderId));
        return mapToResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));
        return mapToResponse(payment);
    }

    @Override
    public PaymentResponse processPayment(Long orderId, PaymentMethod method) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (paymentRepository.findByOrderId(orderId).isPresent())
            throw new IllegalStateException("Payment already exists for order: " + orderId);

        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getTotal())
                .method(method)
                .paymentDate(new Date())
                .transactionRef(UUID.randomUUID().toString())
                .build();

        Payment saved = paymentRepository.save(payment);

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .paymentDate(payment.getPaymentDate())
                .transactionRef(payment.getTransactionRef())
                .orderId(payment.getOrder().getId())
                .build();
    }
}