package tn.esprit.esprit_market.modules.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class CreateDeliveryRequest {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotBlank(message = "Carrier is required")
    private String carrier;

    @NotBlank(message = "Recipient name is required")
    private String recipientName;

    @NotBlank(message = "Recipient phone is required")
    private String recipientPhone;

    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;

    private String deliveryNotes;
    private Date estimatedDeliveryDate;
}