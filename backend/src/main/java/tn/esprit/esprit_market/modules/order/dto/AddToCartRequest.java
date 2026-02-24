package tn.esprit.esprit_market.modules.order.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class AddToCartRequest {

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    private Long productId;
    private Long serviceId;
}