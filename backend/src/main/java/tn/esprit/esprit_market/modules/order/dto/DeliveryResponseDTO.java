package tn.esprit.esprit_market.modules.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.esprit_market.modules.order.enums.DeliveryStatus;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryResponseDTO {
    private Long id;
    private String trackingNumber;
    private String carrier;
    private DeliveryStatus status;
    private Date estimatedDeliveryDate;
    private Date actualDeliveryDate;
    private String recipientName;
    private String recipientPhone;
    private String deliveryAddress;
    private String deliveryNotes;
    private Long orderId;
}
