package tn.esprit.esprit_market.modules.order.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequestDTO {
    private Long productId;
    private Long serviceId;
    
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}
