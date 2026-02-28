package tn.esprit.esprit_market.modules.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.esprit_market.modules.event.enums.EventType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Date is required")
    private Date date;

    @Min(value = 1, message = "Capacity must be at least 1")
    private int capacity;

    private String location;

    private String imageUrl;

    @Min(value = 0, message = "Ticket price cannot be negative")
    private double ticketPrice;

    @NotNull(message = "Event type is required")
    private EventType type;

    private Long organizerId;
}
