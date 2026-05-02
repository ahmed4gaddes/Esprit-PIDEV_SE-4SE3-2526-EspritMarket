package tn.esprit.esprit_market.modules.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.event.dto.ChatMessageRequest;
import tn.esprit.esprit_market.modules.event.dto.ChatMessageResponse;
import tn.esprit.esprit_market.modules.event.entities.ChatMessage;
import tn.esprit.esprit_market.modules.event.entities.LiveSession;
import tn.esprit.esprit_market.modules.event.repositories.ChatMessageRepository;
import tn.esprit.esprit_market.modules.event.repositories.LiveSessionRepository;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.service.IUserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageService implements IChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final LiveSessionRepository liveSessionRepository;
    private final IUserService userService;
    private final ChatModerationService chatModerationService;

    public ChatMessageResponse sendMessage(Long liveSessionId, Long userId, ChatMessageRequest request) {
        LiveSession liveSession = liveSessionRepository.findById(liveSessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Live Session not found with id: " + liveSessionId));

        User user = userService.getUserById(userId);

        // 🛡️ Check moderation BEFORE saving (bad words + spam + active ban)
        chatModerationService.checkAndEnforce(user, liveSession, request.getContent());

        ChatMessage message = ChatMessage.builder()
                .content(request.getContent())
                .liveSession(liveSession)
                .sender(user)
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);

        return mapToResponse(savedMessage);
    }

    public List<ChatMessageResponse> getMessagesByLiveSession(Long liveSessionId) {
        return chatMessageRepository.findByLiveSessionIdOrderBySentAtAsc(liveSessionId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ChatMessageResponse mapToResponse(ChatMessage message) {
        return ChatMessageResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .senderName(message.getSender() != null ? message.getSender().getName() : "Unknown")
                .senderRole(message.getSender() != null ? message.getSender().getRole().name() : null)
                .sentAt(message.getSentAt())
                .build();
    }
}
