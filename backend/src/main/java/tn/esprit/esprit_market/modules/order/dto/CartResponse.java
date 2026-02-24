package tn.esprit.esprit_market.modules.order.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class CartResponse {
    private Long id;
    private Date createdAt;
    private Long userId;
    private List<CartItemResponse> items;
    private double total;
}