package tn.esprit.esprit_market.modules.order.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemResponse {
    private Long id;
    private int quantity;
    private double unitPrice;
    private double subtotal;
    private Long productId;
    private String productName;
    private Long serviceId;
    private String serviceName;
}