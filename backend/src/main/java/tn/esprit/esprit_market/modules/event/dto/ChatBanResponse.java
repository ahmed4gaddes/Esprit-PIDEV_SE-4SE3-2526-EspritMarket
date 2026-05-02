package tn.esprit.esprit_market.modules.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatBanResponse {
    private boolean banned;
    private String reason;       // "BAD_WORD" or "SPAM"
    private LocalDateTime bannedUntil;
    private long secondsRemaining;
}
