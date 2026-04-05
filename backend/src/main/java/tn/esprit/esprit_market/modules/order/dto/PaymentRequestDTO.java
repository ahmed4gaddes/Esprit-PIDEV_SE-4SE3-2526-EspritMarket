package tn.esprit.esprit_market.modules.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.esprit_market.modules.order.enums.PaymentMethod;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {
    @NotNull(message = "Payment method is required")
    private PaymentMethod method;
    
    @Min(value = 0, message = "Amount must be positive")
    private double amount;
    
    private String transactionRef;
}
