package tn.esprit.esprit_market.modules.event.service;

import tn.esprit.esprit_market.modules.event.dto.ChatBanResponse;

/**
 * Thrown when a user tries to send a message while banned.
 * Carries the full ban details as a structured object.
 */
public class ChatBannedException extends RuntimeException {

    private final ChatBanResponse banResponse;

    public ChatBannedException(ChatBanResponse banResponse) {
        super("User is banned from chat");
        this.banResponse = banResponse;
    }

    public ChatBanResponse getBanResponse() {
        return banResponse;
    }
}
