package tn.esprit.esprit_market.modules.order.dto;

import lombok.Builder;
import lombok.Data;
import tn.esprit.esprit_market.modules.order.enums.PaymentMethod;

import java.util.Date;

@Data
@Builder
public class PaymentResponse {
    private Long id;
    private double amount;
    private PaymentMethod method;
    private Date paymentDate;
    private String transactionRef;
    private Long orderId;
}