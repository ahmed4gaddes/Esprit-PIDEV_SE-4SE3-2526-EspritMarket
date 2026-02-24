package tn.esprit.esprit_market.modules.order.dto;

import lombok.Builder;
import lombok.Data;
import tn.esprit.esprit_market.modules.order.enums.OrderStatus;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private double total;
    private OrderStatus status;
    private Date orderDate;
    private String deliveryAddress;
    private Long userId;
    private List<OrderItemResponse> items;
    private PaymentResponse payment;
    private DeliveryResponse delivery;
}