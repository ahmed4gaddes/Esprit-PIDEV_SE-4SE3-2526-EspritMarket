package tn.esprit.esprit_market.modules.order.service;

import tn.esprit.esprit_market.modules.order.dto.PaymentRequestDTO;
import tn.esprit.esprit_market.modules.order.dto.PaymentResponseDTO;

public interface IPaymentService {
    PaymentResponseDTO processPayment(Long orderId, PaymentRequestDTO request);
    PaymentResponseDTO getPaymentByOrderId(Long orderId);
}
