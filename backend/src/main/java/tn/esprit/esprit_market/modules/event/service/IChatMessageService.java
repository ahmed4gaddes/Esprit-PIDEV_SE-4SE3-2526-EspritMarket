package tn.esprit.esprit_market.modules.event.service;

import tn.esprit.esprit_market.modules.event.dto.ChatMessageRequest;
import tn.esprit.esprit_market.modules.event.dto.ChatMessageResponse;

import java.util.List;

public interface IChatMessageService {
    ChatMessageResponse sendMessage(Long liveSessionId, Long userId, ChatMessageRequest request);
    List<ChatMessageResponse> getMessagesByLiveSession(Long liveSessionId);
}
