package tn.esprit.esprit_market.modules.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketRequest {

    @Min(value = 0, message = "Price must be positive")
    private double price;

    @NotNull(message = "User ID is required")
    private Long userId;
}
