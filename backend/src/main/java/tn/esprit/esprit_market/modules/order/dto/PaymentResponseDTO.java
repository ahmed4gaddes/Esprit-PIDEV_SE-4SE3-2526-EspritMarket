package tn.esprit.esprit_market.modules.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.esprit_market.modules.order.enums.PaymentMethod;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private Long id;
    private double amount;
    private PaymentMethod method;
    private Date paymentDate;
    private String transactionRef;
    private Long orderId;
}
