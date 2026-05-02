package tn.esprit.esprit_market.modules.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.esprit_market.modules.event.dto.ChatBanResponse;
import tn.esprit.esprit_market.modules.event.entities.ChatBan;
import tn.esprit.esprit_market.modules.event.entities.LiveSession;
import tn.esprit.esprit_market.modules.event.repositories.ChatBanRepository;
import tn.esprit.esprit_market.modules.user.entity.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatModerationService {

    private final ChatBanRepository chatBanRepository;

    // ─── Ban duration ────────────────────────────────────────────────────────
    private static final int BAN_DURATION_MINUTES = 2;

    // ─── Spam detection config ───────────────────────────────────────────────
    /** Max messages allowed in the spam window */
    private static final int SPAM_MAX_MESSAGES = 4;
    /** Spam detection window in seconds */
    private static final int SPAM_WINDOW_SECONDS = 8;

    // ─── Bad words list ───────────────────────────────────────────────────────
    private static final Set<String> BAD_WORDS = Set.of(
            // English
            "fuck", "shit", "bitch", "asshole", "bastard", "crap", "dick",
            "pussy", "idiot", "stupid", "moron", "loser", "retard", "dumb", "trash",
            // French
            "merde", "putain", "connard", "salaud", "imbecile", "cretin",
            "abruti", "con", "ferme", "gueule",
            // Arabic / Tunisian transliterated
            "kalb", "7mar", "zbi", "kess", "nbik", "zemel", "ghabi",
            "baaed", "khawa", "ya9lak", "tfahen", "wa9ef", "barra"
    );

    /**
     * Main entry point — call before saving any chat message.
     * Throws 403 ResponseStatusException if user is banned.
     */
    public void checkAndEnforce(User user, LiveSession liveSession, String messageContent) {
        Long userId = user.getId();
        Long sessionId = liveSession.getId();

        // 1. Check existing active ban
        Optional<ChatBan> existingBan = chatBanRepository.findActiveBan(userId, sessionId, LocalDateTime.now());
        if (existingBan.isPresent()) {
            throwBannedException(existingBan.get());
        }

        // 2. Check bad words
        if (containsBadWord(messageContent)) {
            ChatBan ban = createBan(user, liveSession, "BAD_WORD");
            throwBannedException(ban);
        }

        // 3. Check spam (too many messages in short time)
        Date since = new Date(System.currentTimeMillis() - (long) SPAM_WINDOW_SECONDS * 1000);
        long recentCount = chatBanRepository.countRecentMessages(userId, sessionId, since);
        if (recentCount >= SPAM_MAX_MESSAGES) {
            ChatBan ban = createBan(user, liveSession, "SPAM");
            throwBannedException(ban);
        }
    }

    /**
     * Check if user is currently banned — used by GET /ban-status endpoint.
     */
    public ChatBanResponse getBanStatus(Long userId, Long liveSessionId) {
        Optional<ChatBan> ban = chatBanRepository.findActiveBan(userId, liveSessionId, LocalDateTime.now());
        if (ban.isEmpty()) {
            return ChatBanResponse.builder().banned(false).build();
        }
        ChatBan b = ban.get();
        long seconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), b.getBannedUntil());
        return ChatBanResponse.builder()
                .banned(true)
                .reason(b.getReason())
                .bannedUntil(b.getBannedUntil())
                .secondsRemaining(Math.max(0, seconds))
                .build();
    }

    // ─── Private helpers ─────────────────────────────────────────────────────

    private boolean containsBadWord(String content) {
        if (content == null) return false;
        String lower = content.toLowerCase();
        for (String word : BAD_WORDS) {
            if (lower.contains(word)) return true;
        }
        return false;
    }

    private ChatBan createBan(User user, LiveSession liveSession, String reason) {
        ChatBan ban = ChatBan.builder()
                .user(user)
                .liveSession(liveSession)
                .bannedUntil(LocalDateTime.now().plusMinutes(BAN_DURATION_MINUTES))
                .reason(reason)
                .build();
        return chatBanRepository.save(ban);
    }

    private void throwBannedException(ChatBan ban) {
        long seconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), ban.getBannedUntil());
        ChatBanResponse banResponse = ChatBanResponse.builder()
                .banned(true)
                .reason(ban.getReason())
                .bannedUntil(ban.getBannedUntil())
                .secondsRemaining(Math.max(0, seconds))
                .build();
        throw new ChatBannedException(banResponse);
    }
}
