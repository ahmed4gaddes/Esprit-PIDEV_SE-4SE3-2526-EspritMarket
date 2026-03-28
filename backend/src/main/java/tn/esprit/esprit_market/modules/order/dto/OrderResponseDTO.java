package tn.esprit.esprit_market.modules.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.esprit_market.modules.order.enums.OrderStatus;
import tn.esprit.esprit_market.modules.order.enums.PaymentMethod;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    private Long id;
    private Date createdAt;
    private OrderStatus status;
    private String shippingAddress;
    private PaymentMethod paymentMethod;
    private double totalAmount;
    private Long userId;
    private String userName;
    private List<OrderItemResponseDTO> items;
    private PaymentResponseDTO payment;
    private DeliveryResponseDTO delivery;
}
