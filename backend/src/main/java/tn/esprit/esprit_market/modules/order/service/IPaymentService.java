package tn.esprit.esprit_market.modules.order.service;

import tn.esprit.esprit_market.modules.order.dto.PaymentResponse;
import tn.esprit.esprit_market.modules.order.enums.PaymentMethod;

import java.util.List;

public interface IPaymentService {
    PaymentResponse getPaymentByOrderId(Long orderId);
    PaymentResponse getPaymentById(Long paymentId);
    PaymentResponse processPayment(Long orderId, PaymentMethod method);
    List<PaymentResponse> getAllPayments();
}