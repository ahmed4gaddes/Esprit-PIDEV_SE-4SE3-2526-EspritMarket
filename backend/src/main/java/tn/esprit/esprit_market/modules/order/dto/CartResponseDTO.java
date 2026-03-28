package tn.esprit.esprit_market.modules.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDTO {
    private Long id;
    private Date createdAt;
    private Long userId;
    private List<CartItemResponseDTO> items;
    private double subtotal;
    private double deliveryFee;
    private double total;
}
