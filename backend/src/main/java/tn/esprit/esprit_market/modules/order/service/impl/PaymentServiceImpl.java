package tn.esprit.esprit_market.modules.order.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.order.dto.PaymentRequestDTO;
import tn.esprit.esprit_market.modules.order.dto.PaymentResponseDTO;
import tn.esprit.esprit_market.modules.order.entity.Order;
import tn.esprit.esprit_market.modules.order.entity.Payment;
import tn.esprit.esprit_market.modules.order.enums.OrderStatus;
import tn.esprit.esprit_market.modules.order.mapper.OrderMapper;
import tn.esprit.esprit_market.modules.order.repository.OrderRepository;
import tn.esprit.esprit_market.modules.order.repository.PaymentRepository;
import tn.esprit.esprit_market.modules.order.service.IPaymentService;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements IPaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public PaymentResponseDTO processPayment(Long orderId, PaymentRequestDTO request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        if (paymentRepository.findByOrderId(orderId).isPresent()) {
            throw new IllegalArgumentException("Payment already processed for this order");
        }

        Payment payment = Payment.builder()
                .order(order)
                .amount(request.getAmount())
                .method(request.getMethod())
                .transactionRef(request.getTransactionRef() != null ? request.getTransactionRef() : "TXN-" + System.currentTimeMillis())
                .paymentDate(new Date())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        
        // Update order status
        order.setStatus(OrderStatus.PAID);
        order.setPayment(savedPayment);
        orderRepository.save(order);

        return orderMapper.toPaymentResponseDTO(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order: " + orderId));
        return orderMapper.toPaymentResponseDTO(payment);
    }
}
