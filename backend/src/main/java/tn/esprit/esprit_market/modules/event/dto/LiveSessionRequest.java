package tn.esprit.esprit_market.modules.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LiveSessionRequest {

    @NotBlank(message = "Link is required")
    private String link;

    @NotBlank(message = "Platform is required")
    private String platform;

    @NotNull(message = "Start time is required")
    private Date startTime;
}
