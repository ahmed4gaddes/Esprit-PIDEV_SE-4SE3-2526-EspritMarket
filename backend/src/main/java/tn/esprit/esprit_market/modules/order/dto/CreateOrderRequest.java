package tn.esprit.esprit_market.modules.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import tn.esprit.esprit_market.modules.order.enums.PaymentMethod;

@Data
public class CreateOrderRequest {

    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;

    private PaymentMethod paymentMethod;
}