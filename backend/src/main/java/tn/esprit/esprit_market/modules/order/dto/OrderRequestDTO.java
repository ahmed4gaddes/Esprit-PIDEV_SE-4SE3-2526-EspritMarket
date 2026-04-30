package tn.esprit.esprit_market.modules.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.esprit_market.modules.order.enums.PaymentMethod;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {
    private PaymentMethod paymentMethod;
    
    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;
    
    // Optional delivery details
    private String recipientName;
    private String recipientPhone;
    private String deliveryNotes;

    // F2 & F7: Promo and Loyalty
    private String promoCode;
    private boolean useLoyaltyPoints;
}
