package tn.esprit.esprit_market.modules.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDTO {
    private Long id;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
    private Long productId;
    private String productName;
    private String productImageUrl;
    private Long serviceId;
    private String serviceName;
}
