package tn.esprit.esprit_market.modules.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRequestDTO {
    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;
    
    private String recipientName;
    private String recipientPhone;
    private String deliveryNotes;
}
